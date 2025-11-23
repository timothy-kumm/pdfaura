# API Generation Setup

This document explains how to use the automated API generation for the PDF Vision frontend.

## Overview

The frontend automatically generates TypeScript API clients and types from the backend's OpenAPI specification. This ensures type safety and keeps the frontend synchronized with backend changes.

## Prerequisites

1. Backend server running on `http://localhost:8080`
2. Node.js and npm installed
3. Dependencies installed: `npm install`

## Usage

### Generate API Client

```bash
# Generate from running backend
npm run generate-api

# Generate from local OpenAPI JSON file
npm run generate-api-local
```

### Manual Generation

```bash
# Run the generation script directly
bash scripts/generate-api.sh
```

## What Gets Generated

- **API Client**: TypeScript axios-based client in `src/generated/api/`
- **Types**: All DTOs and model types
- **Interfaces**: Request/response interfaces

## Integration

### Using the Generated API

```typescript
// Import the generated API
import { DefaultApi, Configuration, PdfUploadResponse } from '@/generated/api'

// Create API instance
const config = new Configuration({ basePath: 'http://localhost:8080' })
const api = new DefaultApi(config)

// Use the API
const response = await api.uploadPdf(file)
```

### Using the Composable

```vue
<script setup lang="ts">
import { usePdfApi } from '@/composables/usePdfApi'

const { loading, error, uploadPdf, getPdfInfo } = usePdfApi()

const handleFileUpload = async (file: File) => {
  try {
    const result = await uploadPdf(file)
    console.log('Upload successful:', result)
  } catch (err) {
    console.error('Upload failed:', error.value)
  }
}
</script>
```

## Configuration

### Environment Variables

```bash
# .env
VITE_API_BASE_URL=http://localhost:8080
```

### OpenAPI Generator Config

Configuration is in `openapitools.json`:

```json
{
  "generator-cli": {
    "version": "7.10.0",
    "generators": {
      "typescript-axios": {
        "additionalProperties": {
          "supportsES6": true,
          "stringEnums": true,
          "enumPropertyNaming": "PascalCase"
        }
      }
    }
  }
}
```

## Workflow

1. **Backend Development**: Add new endpoints with OpenAPI annotations
2. **Generate Client**: Run `npm run generate-api` to update frontend types
3. **Frontend Development**: Use generated types and API client
4. **Type Safety**: TypeScript ensures compatibility between frontend and backend

## Files Generated

- `src/generated/api/api.ts` - Main API client
- `src/generated/api/base.ts` - Base API functionality
- `src/generated/api/common.ts` - Common utilities
- `src/generated/api/configuration.ts` - Configuration types
- `src/generated/api/models/` - All model types and DTOs

## Troubleshooting

### Backend Not Running
```
❌ Backend not running at http://localhost:8080/api-docs
Please start the backend server first with: ./gradlew bootRun
```

### Invalid JSON
```
❌ Invalid JSON received from API
```
Check that the backend OpenAPI endpoint is working correctly.

### Permission Denied
```bash
chmod +x scripts/generate-api.sh
```

## Best Practices

1. **Regenerate After Backend Changes**: Always regenerate the API client after backend updates
2. **Version Control**: Generated files are in `.gitignore` - don't commit them
3. **Type Safety**: Use the generated types throughout your application
4. **Error Handling**: Use the provided error handling in the composable
5. **Environment Configuration**: Use environment variables for API URLs