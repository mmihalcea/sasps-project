# Keycloak Configuration Script for Windows PowerShell
# This script sets up the SASPS realm, client, roles, test users AND database users

$KEYCLOAK_URL = "http://localhost:8090"
$ADMIN_USER = "admin"
$ADMIN_PASSWORD = "admin"
$REALM = "sasps-realm"
$CLIENT_ID = "sasps-client"

# Database configuration
$DB_CONTAINER = "postgres-db"
$DB_USER = "appuser"
$DB_NAME = "appdb"

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "SASPS Project - Complete Setup Script" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# ============================================
# STEP 1: Setup PostgreSQL Database Users
# ============================================
Write-Host "STEP 1: Setting up PostgreSQL database..." -ForegroundColor Magenta

# Wait for PostgreSQL to be ready
Write-Host "Waiting for PostgreSQL to be ready..." -ForegroundColor Yellow
$maxRetries = 30
$retryCount = 0
do {
    $result = docker exec $DB_CONTAINER pg_isready -U $DB_USER -d $DB_NAME 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  PostgreSQL is ready!" -ForegroundColor Green
        break
    }
    $retryCount++
    Write-Host "  Waiting for PostgreSQL... ($retryCount/$maxRetries)" -ForegroundColor Gray
    Start-Sleep -Seconds 2
} while ($retryCount -lt $maxRetries)

if ($retryCount -ge $maxRetries) {
    Write-Host "  PostgreSQL is not ready after $maxRetries attempts. Exiting." -ForegroundColor Red
    exit 1
}

# Insert counties if they don't exist
Write-Host "Inserting counties..." -ForegroundColor Yellow
$countiesSQL = @"
INSERT INTO county (id, name) VALUES 
(1, 'Alba'), (2, 'Arad'), (3, 'Arges'), (4, 'Bacau'), (5, 'Bihor'),
(6, 'Bistrita-Nasaud'), (7, 'Botosani'), (8, 'Braila'), (9, 'Brasov'), (10, 'Bucuresti'),
(11, 'Buzau'), (12, 'Calarasi'), (13, 'Caras-Severin'), (14, 'Cluj'), (15, 'Constanta'),
(16, 'Covasna'), (17, 'Dambovita'), (18, 'Dolj'), (19, 'Galati'), (20, 'Giurgiu'),
(21, 'Gorj'), (22, 'Harghita'), (23, 'Hunedoara'), (24, 'Ialomita'), (25, 'Iasi'),
(26, 'Ilfov'), (27, 'Maramures'), (28, 'Mehedinti'), (29, 'Mures'), (30, 'Neamt'),
(31, 'Olt'), (32, 'Prahova'), (33, 'Salaj'), (34, 'Satu Mare'), (35, 'Sibiu'),
(36, 'Suceava'), (37, 'Teleorman'), (38, 'Timis'), (39, 'Tulcea'), (40, 'Valcea'),
(41, 'Vaslui'), (42, 'Vrancea')
ON CONFLICT (id) DO NOTHING;
"@
docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c $countiesSQL 2>$null
Write-Host "  Counties inserted/verified" -ForegroundColor Green

# Insert test users
Write-Host "Inserting test users..." -ForegroundColor Yellow
$usersSQL = @"
INSERT INTO users (id, name, email, phone, county, city, address, active, email_notifications_enabled, sms_notifications_enabled, reminder_hours_before, user_role, password)
VALUES 
(1, 'Admin User', 'admin@test.com', '0700000001', 'Bucuresti', 'Bucuresti', 'Str. Administratorilor 1', true, true, true, 24, 'ADMIN', 'admin123'),
(2, 'Regular User', 'user@test.com', '0700000002', 'Cluj', 'Cluj-Napoca', 'Str. Utilizatorilor 2', true, true, false, 24, 'USER', 'user123'),
(3, 'Test Client', 'test@example.com', '0700000003', 'Timis', 'Timisoara', 'Str. Testelor 3', true, true, true, 48, 'USER', 'test123')
ON CONFLICT (id) DO UPDATE SET 
  name = EXCLUDED.name,
  email = EXCLUDED.email,
  password = EXCLUDED.password,
  user_role = EXCLUDED.user_role;
"@
docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c $usersSQL 2>$null
Write-Host "  Test users inserted/updated" -ForegroundColor Green

# Reset sequence for users table
docker exec $DB_CONTAINER psql -U $DB_USER -d $DB_NAME -c "SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));" 2>$null | Out-Null

Write-Host "  Database setup completed!" -ForegroundColor Green
Write-Host ""

# ============================================
# STEP 2: Load Institutions via API
# ============================================
Write-Host "STEP 2: Loading institutions..." -ForegroundColor Magenta

$BACKEND_URL = "http://localhost:8080"

# Wait for backend to be ready
Write-Host "Waiting for backend to be ready..." -ForegroundColor Yellow
$maxRetries = 30
$retryCount = 0
do {
    try {
        $response = Invoke-WebRequest -Uri "$BACKEND_URL/api/institution" -Method GET -UseBasicParsing -TimeoutSec 5 2>$null
        if ($response.StatusCode -eq 200) {
            Write-Host "  Backend is ready!" -ForegroundColor Green
            break
        }
    } catch {
        # Backend not ready yet
    }
    $retryCount++
    Write-Host "  Waiting for backend... ($retryCount/$maxRetries)" -ForegroundColor Gray
    Start-Sleep -Seconds 2
} while ($retryCount -lt $maxRetries)

# Check if institutions already exist
try {
    $institutions = Invoke-RestMethod -Uri "$BACKEND_URL/api/institution" -Method GET
    if ($institutions.Count -eq 0) {
        Write-Host "Loading institutions via API..." -ForegroundColor Yellow
        $loadResult = Invoke-RestMethod -Uri "$BACKEND_URL/api/institution/load-data" -Method POST
        Write-Host "  $loadResult" -ForegroundColor Green
    } else {
        Write-Host "  Institutions already exist (count: $($institutions.Count))" -ForegroundColor Cyan
    }
} catch {
    Write-Host "  Warning: Could not load institutions - $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host ""

# ============================================
# STEP 3: Setup Keycloak
# ============================================
Write-Host "STEP 3: Setting up Keycloak..." -ForegroundColor Magenta
Write-Host "Waiting for Keycloak to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Get admin access token
Write-Host "Getting admin access token..." -ForegroundColor Yellow
try {
  $tokenResponse = Invoke-RestMethod -Uri "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" `
    -Method POST `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
      username = $ADMIN_USER
      password = $ADMIN_PASSWORD
      grant_type = "password"
      client_id = "admin-cli"
    }

  $ADMIN_TOKEN = $tokenResponse.access_token

  if ([string]::IsNullOrEmpty($ADMIN_TOKEN)) {
    Write-Host "Failed to get admin token. Check if Keycloak is running." -ForegroundColor Red
    exit 1
  }

  Write-Host "Admin token obtained successfully" -ForegroundColor Green
} catch {
  Write-Host "Failed to connect to Keycloak: $($_.Exception.Message)" -ForegroundColor Red
  exit 1
}

# Create Realm
Write-Host "Creating realm: $REALM..." -ForegroundColor Yellow
try {
  Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
    -ContentType "application/json" `
    -Body (@{
      realm = $REALM
      enabled = $true
      displayName = "SASPS Appointment System"
      accessTokenLifespan = 3600
      ssoSessionMaxLifespan = 36000
    } | ConvertTo-Json) | Out-Null
  Write-Host "  Realm created successfully" -ForegroundColor Green
} catch {
  if ($_.Exception.Response.StatusCode.value__ -eq 409) {
    Write-Host "  Realm already exists" -ForegroundColor Cyan
  } else {
    Write-Host "  Failed to create realm" -ForegroundColor Red
  }
}

# Create Client
Write-Host "Creating client: $CLIENT_ID..." -ForegroundColor Yellow
try {
  Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/clients" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
    -ContentType "application/json" `
    -Body (@{
      clientId = $CLIENT_ID
      enabled = $true
      publicClient = $true
      directAccessGrantsEnabled = $true
      standardFlowEnabled = $true
      implicitFlowEnabled = $false
      serviceAccountsEnabled = $false
      redirectUris = @("http://localhost:4200/*", "http://localhost:8080/*")
      webOrigins = @("http://localhost:4200", "http://localhost:8080")
      protocol = "openid-connect"
    } | ConvertTo-Json) | Out-Null
  Write-Host "  Client created successfully" -ForegroundColor Green
} catch {
  if ($_.Exception.Response.StatusCode.value__ -eq 409) {
    Write-Host "  Client already exists" -ForegroundColor Cyan
  } else {
    Write-Host "  Failed to create client" -ForegroundColor Red
  }
}

# Create Roles
Write-Host "Creating roles..." -ForegroundColor Yellow
try {
  Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/roles" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
    -ContentType "application/json" `
    -Body (@{ name = "ADMIN"; description = "Administrator role" } | ConvertTo-Json) | Out-Null
  Write-Host "  ADMIN role created" -ForegroundColor Green
} catch {
  if ($_.Exception.Response.StatusCode.value__ -eq 409) {
    Write-Host "  ADMIN role already exists" -ForegroundColor Cyan
  }
}

try {
  Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/roles" `
    -Method POST `
    -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
    -ContentType "application/json" `
    -Body (@{ name = "USER"; description = "Regular user role" } | ConvertTo-Json) | Out-Null
  Write-Host "  USER role created" -ForegroundColor Green
} catch {
  if ($_.Exception.Response.StatusCode.value__ -eq 409) {
    Write-Host "  USER role already exists" -ForegroundColor Cyan
  }
}

# Get role IDs
$adminRole = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/roles/ADMIN" `
  -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" }
$ADMIN_ROLE_ID = $adminRole.id

$userRole = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/roles/USER" `
  -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" }
$USER_ROLE_ID = $userRole.id

# Function to create user and assign role
function Create-KeycloakUser {
  param(
    [string]$Username,
    [string]$Email,
    [string]$FirstName,
    [string]$LastName,
    [string]$Password,
    [string]$RoleId,
    [string]$RoleName
  )
  
  Write-Host "Creating user: $Username..." -ForegroundColor Yellow
  
  try {
    Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/users" `
      -Method POST `
      -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
      -ContentType "application/json" `
      -Body (@{
        username = $Username
        email = $Email
        firstName = $FirstName
        lastName = $LastName
        enabled = $true
        emailVerified = $true
        credentials = @(@{
          type = "password"
          value = $Password
          temporary = $false
        })
      } | ConvertTo-Json -Depth 10) | Out-Null
    Write-Host "  User created" -ForegroundColor Green
  } catch {
    if ($_.Exception.Response.StatusCode.value__ -eq 409) {
      Write-Host "  User already exists" -ForegroundColor Cyan
    } else {
      Write-Host "  Failed to create user" -ForegroundColor Red
      return
    }
  }
  
  Start-Sleep -Seconds 1
  
  try {
    $users = Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/users?username=$Username" `
      -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" }
    
    if ($users.Count -gt 0) {
      $userId = $users[0].id
      
      Invoke-RestMethod -Uri "$KEYCLOAK_URL/admin/realms/$REALM/users/$userId/role-mappings/realm" `
        -Method POST `
        -Headers @{ Authorization = "Bearer $ADMIN_TOKEN" } `
        -ContentType "application/json" `
        -Body (@(@{ id = $RoleId; name = $RoleName }) | ConvertTo-Json) | Out-Null
      Write-Host "  $RoleName role assigned" -ForegroundColor Green
    }
  } catch {
    Write-Host "  Warning: Could not assign role" -ForegroundColor Yellow
  }
}

# Create users
Create-KeycloakUser -Username "admin@test.com" -Email "admin@test.com" `
  -FirstName "Admin" -LastName "User" -Password "admin123" `
  -RoleId $ADMIN_ROLE_ID -RoleName "ADMIN"

Create-KeycloakUser -Username "user@test.com" -Email "user@test.com" `
  -FirstName "Regular" -LastName "User" -Password "user123" `
  -RoleId $USER_ROLE_ID -RoleName "USER"

Create-KeycloakUser -Username "test@example.com" -Email "test@example.com" `
  -FirstName "Test" -LastName "Client" -Password "test123" `
  -RoleId $USER_ROLE_ID -RoleName "USER"

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "SASPS Project Setup Completed!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "DATABASE USERS (for app login):" -ForegroundColor Cyan
Write-Host "  Admin: admin@test.com / admin123 (ADMIN role)"
Write-Host "  User:  user@test.com / user123 (USER role)"
Write-Host "  Test:  test@example.com / test123 (USER role)"
Write-Host ""
Write-Host "KEYCLOAK CONFIGURATION:" -ForegroundColor Cyan
Write-Host "  Realm: $REALM"
Write-Host "  Client ID: $CLIENT_ID"
Write-Host "  Admin Console: $KEYCLOAK_URL/admin"
Write-Host "  Admin credentials: $ADMIN_USER / $ADMIN_PASSWORD"
Write-Host ""
Write-Host "SERVICES:" -ForegroundColor Cyan
Write-Host "  Frontend:  http://localhost:4200"
Write-Host "  Backend:   http://localhost:8080"
Write-Host "  Keycloak:  http://localhost:8090"
Write-Host "  SonarQube: http://localhost:9000"
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Test authentication:"
Write-Host '  $body = @{ username="user@test.com"; password="user123"; grant_type="password"; client_id="sasps-client" }' -ForegroundColor Cyan
Write-Host "  `$token = (Invoke-RestMethod -Uri '$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token' -Method POST -ContentType 'application/x-www-form-urlencoded' -Body `$body).access_token" -ForegroundColor Cyan
Write-Host '  Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/user/3" -Headers @{ Authorization="Bearer $token" }' -ForegroundColor Cyan
