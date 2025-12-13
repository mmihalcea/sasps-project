#!/bin/bash

# Keycloak Configuration Script
# This script sets up the SASPS realm, client, roles, and test users

KEYCLOAK_URL="http://localhost:8090"
ADMIN_USER="admin"
ADMIN_PASSWORD="admin"
REALM="sasps-realm"
CLIENT_ID="sasps-client"

echo "Waiting for Keycloak to start..."
sleep 30

# Get admin access token
echo "Getting admin access token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
  echo "Failed to get admin token. Check if Keycloak is running."
  exit 1
fi

echo "Admin token obtained successfully"

# Create Realm
echo "Creating realm: $REALM..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realm": "'$REALM'",
    "enabled": true,
    "displayName": "SASPS Appointment System",
    "accessTokenLifespan": 3600,
    "ssoSessionMaxLifespan": 36000
  }'

# Create Client
echo "Creating client: $CLIENT_ID..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "'$CLIENT_ID'",
    "enabled": true,
    "publicClient": true,
    "directAccessGrantsEnabled": true,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "serviceAccountsEnabled": false,
    "redirectUris": ["http://localhost:4200/*", "http://localhost:8080/*"],
    "webOrigins": ["http://localhost:4200", "http://localhost:8080"],
    "protocol": "openid-connect"
  }'

# Create Roles
echo "Creating roles..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "ADMIN", "description": "Administrator role"}'

curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/roles" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name": "USER", "description": "Regular user role"}'

# Get role IDs
ADMIN_ROLE_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/roles/ADMIN" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

USER_ROLE_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/roles/USER" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')

# Create Admin User
echo "Creating admin user..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@test.com",
    "email": "admin@test.com",
    "firstName": "Admin",
    "lastName": "User",
    "enabled": true,
    "emailVerified": true,
    "credentials": [{
      "type": "password",
      "value": "admin123",
      "temporary": false
    }]
  }'

# Get admin user ID
ADMIN_USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/users?username=admin@test.com" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

# Assign ADMIN role to admin user
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users/$ADMIN_USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[{"id": "'$ADMIN_ROLE_ID'", "name": "ADMIN"}]'

# Create Regular User
echo "Creating regular user..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@test.com",
    "email": "user@test.com",
    "firstName": "Regular",
    "lastName": "User",
    "enabled": true,
    "emailVerified": true,
    "credentials": [{
      "type": "password",
      "value": "user123",
      "temporary": false
    }]
  }'

# Get regular user ID
REGULAR_USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/users?username=user@test.com" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

# Assign USER role to regular user
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users/$REGULAR_USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[{"id": "'$USER_ROLE_ID'", "name": "USER"}]'

# Create Test Client
echo "Creating test client..."
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "Client",
    "enabled": true,
    "emailVerified": true,
    "credentials": [{
      "type": "password",
      "value": "test123",
      "temporary": false
    }]
  }'

# Get test user ID
TEST_USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/users?username=test@example.com" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')

# Assign USER role to test user
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users/$TEST_USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[{"id": "'$USER_ROLE_ID'", "name": "USER"}]'

echo ""
echo "=========================================="
echo "Keycloak configuration completed!"
echo "=========================================="
echo "Realm: $REALM"
echo "Client ID: $CLIENT_ID"
echo ""
echo "Test Users:"
echo "  Admin: admin@test.com / admin123 (ADMIN role)"
echo "  User:  user@test.com / user123 (USER role)"
echo "  Test:  test@example.com / test123 (USER role)"
echo ""
echo "Keycloak Admin Console: $KEYCLOAK_URL/admin"
echo "Admin credentials: $ADMIN_USER / $ADMIN_PASSWORD"
echo "=========================================="
