#!/bin/bash

# API Generation Script for PDF Vision Frontend

set -e

API_URL="http://localhost:8080/api-docs"
OUTPUT_DIR="src/generated/api"
TEMP_FILE="openapi.json"

echo "🚀 Starting API generation..."

# Check if backend is running
if ! curl -s --connect-timeout 5 "$API_URL" > /dev/null; then
    echo "❌ Backend not running at $API_URL"
    echo "Please start the backend server first with: ./gradlew bootRun"
    exit 1
fi

echo "✅ Backend is running"

# Clean previous generated files
if [ -d "$OUTPUT_DIR" ]; then
    echo "🧹 Cleaning previous generated files..."
    rm -rf "$OUTPUT_DIR"
fi

# Download OpenAPI spec
echo "📥 Downloading OpenAPI specification..."
curl -s "$API_URL" > "$TEMP_FILE"

# Validate JSON
if ! jq . "$TEMP_FILE" > /dev/null 2>&1; then
    echo "❌ Invalid JSON received from API"
    rm -f "$TEMP_FILE"
    exit 1
fi

echo "✅ OpenAPI specification downloaded"

# Generate API client
echo "🔄 Generating TypeScript API client..."
npx openapi-generator-cli generate \
    -i "$TEMP_FILE" \
    -g typescript-axios \
    -o "$OUTPUT_DIR" \
    --additional-properties=supportsES6=true,npmName=@/generated/api,stringEnums=true,enumPropertyNaming=PascalCase

# Clean up
rm -f "$TEMP_FILE"

echo "✅ API generation completed!"
echo "📁 Generated files are located in: $OUTPUT_DIR"
echo ""
echo "Usage in your Vue components:"
echo "import { DefaultApi, Configuration } from '@/generated/api';"
echo ""
echo "const config = new Configuration({ basePath: 'http://localhost:8080' });"
echo "const api = new DefaultApi(config);"