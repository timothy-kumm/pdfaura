Set-StrictMode -Version Latest

$apiUrl = "http://localhost:8080/api-docs"
$outputDir = ".\src\generated\api"
$tempFile = ".\openapi.json"

# Clean previous generated files
Remove-Item -Path $outputDir -Recurse -Force -ErrorAction SilentlyContinue

# Download OpenAPI spec
Invoke-WebRequest -Uri $apiUrl -OutFile $tempFile -UseBasicParsing

# Validate JSON
Get-Content -Path $tempFile -Raw | ConvertFrom-Json | Out-Null

# Generate API client
npx openapi-generator-cli generate `
    -i "$tempFile" `
    -g typescript-axios `
    -o "$outputDir" `
    --additional-properties="supportsES6=true,npmName=@/generated/api,stringEnums=true,enumPropertyNaming=PascalCase,skipPackageJson=true,skipTsconfigJson=true,skipGitIgnore=true,skipNpmIgnore=true,skipReadme=true,withSeparateModelsAndApi=true,usePromises=true,withoutSufix=true,apiPackage=api,modelPackage=model"

# Clean up
Remove-Item -Path $tempFile -Force -ErrorAction SilentlyContinue