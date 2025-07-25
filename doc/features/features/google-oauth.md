# Concept

The Google OAuth feature allows users to authenticate using their Google accounts instead of traditional username/password credentials. This implementation follows the OAuth 2.0 authorization code flow, where users are redirected to Google's authentication server, grant permission, and return with an authorization code that is exchanged for user information and authentication tokens.

The feature supports automatic user registration for new users and can optionally integrate with business services to synchronize user data.

# Set up

The Google OAuth feature is provided by the `auth-oauth-google` module and requires proper configuration to work with Google's OAuth 2.0 services.

## Configuration

Add the following configuration to your application configuration file:

```yaml
google:
  oauth:
    client-id: "your-google-client-id"
    client-secret: "your-google-client-secret"
    redirect-uri: "https://yourapp.com/oauth/callback"
    business-callback-url: "https://business-service.com/users" # Optional: for business integration
    default-roles:
      - "USER"
```

## Google Cloud Console Setup

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google+ API
4. Create OAuth 2.0 credentials (Web application)
5. Configure authorized redirect URIs to match your `redirect-uri` configuration

# Example for REST API

## Get Google Authentication URL

**Endpoint:** `GET /v1/oauth2/google/auth-url`

**Description:** Redirects users to Google's OAuth consent page.

**Output:** 302 Redirect to Google OAuth URL

```
Location: https://accounts.google.com/oauth/authorize?client_id=your-client-id&redirect_uri=https://yourapp.com/oauth/callback&response_type=code&scope=email%20profile
```

## Authenticate with Google OAuth Code

**Endpoint:** `POST /v1/oauth2/google/code`

**Description:** Exchanges the authorization code received from Google for authentication tokens.

**Input:** POST request with authorization code

```json
{
  "code": "4/0AfJohKjsxxx...authorization-code-from-google"
}
```

**Output:** 200 OK with access token and refresh token

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

# Flow Description

## New User Flow

1. User clicks "Login with Google" button
2. Application redirects to `GET /v1/oauth2/google/auth-url`
3. User is redirected to Google's OAuth consent page
4. User grants permission and is redirected back with authorization code
5. Application calls `POST /v1/oauth2/google/code` with the authorization code
6. System exchanges code for access token with Google
7. System retrieves user information from Google (email, name, Google ID)
8. System creates new `OauthUser` with:
   - Generated username from email prefix
   - Verified email address
   - Default roles (configured)
   - Google ID for future authentication
   - Business ID (if business callback is configured)
9. System returns authentication tokens

## Existing User Flow

1. Same steps 1-7 as new user flow
2. System finds existing user by email address
3. System authenticates existing user
4. System returns authentication tokens

## Business Integration (Optional)

When `business-callback-url` is configured:

1. For new users, system sends user data to business service:
   ```json
   {
     "userId": "uuid-of-user",
     "firstName": "John",
     "lastName": "Doe", 
     "email": "john.doe@example.com"
   }
   ```
2. Business service returns business user ID
3. User is created with associated business ID

# Error Handling

## Invalid Authorization Code

**Status:** 500 Internal Server Error
**Cause:** Invalid or expired authorization code from Google

## Google API Errors

**Status:** 500 Internal Server Error  
**Cause:** Network issues or Google service unavailability

## Validation Errors

**Status:** 400 Bad Request
**Cause:** Missing or empty authorization code in request

# Security Considerations

- Users authenticated via Google OAuth are automatically verified (no email verification needed)
- Google ID is stored for future authentication attempts
- No password is stored for OAuth users
- Account activation type is set to `GOOGLE_OAUTH`
- Users cannot change to password-based authentication without admin intervention
