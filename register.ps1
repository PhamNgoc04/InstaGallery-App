$payload = Get-Content -Path "payload.json" -Raw

try {
    $response = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/v1/auth/register" -Method Post -ContentType "application/json; charset=utf-8" -Body $payload
    Write-Host "Success:"
    $response | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error details:"
    Write-Host $_.Exception.Message
    if ($_.ErrorDetails) {
        Write-Host $_.ErrorDetails.Message
    }
}
