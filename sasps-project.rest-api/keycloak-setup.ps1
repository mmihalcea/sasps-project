# Keycloak Configuration Script for Windows PowerShell
# This script sets up the SASPS realm, client, roles, and test users

$KEYCLOAK_URL = "http://localhost:8090"
$ADMIN_USER = "admin"
$ADMIN_PASSWORD = "admin"
$REALM = "sasps-realm"
$CLIENT_ID = "sasps-client"

Write-Host "Waiting for Keycloak to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

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
Write-Host "Keycloak configuration completed!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "Realm: $REALM"
Write-Host "Client ID: $CLIENT_ID"
Write-Host ""
Write-Host "Test Users:"
Write-Host "  Admin: admin@test.com / admin123 (ADMIN role)"
Write-Host "  User:  user@test.com / user123 (USER role)"
Write-Host "  Test:  test@example.com / test123 (USER role)"
Write-Host ""
Write-Host "Keycloak Admin Console: $KEYCLOAK_URL/admin"
Write-Host "Admin credentials: $ADMIN_USER / $ADMIN_PASSWORD"
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Test authentication:"
Write-Host '  $body = @{ username="user@test.com"; password="user123"; grant_type="password"; client_id="sasps-client" }' -ForegroundColor Cyan
Write-Host "  `$token = (Invoke-RestMethod -Uri '$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token' -Method POST -ContentType 'application/x-www-form-urlencoded' -Body `$body).access_token" -ForegroundColor Cyan
Write-Host '  Invoke-RestMethod -Uri "http://localhost:8080/api/notifications/user/3" -Headers @{ Authorization="Bearer $token" }' -ForegroundColor Cyan
