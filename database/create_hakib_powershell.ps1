# CSMS Database Backup to hakib file
# PowerShell Script

Write-Host "CSMS Database Backup Script" -ForegroundColor Green
Write-Host "===========================" -ForegroundColor Green
Write-Host ""

# Set PostgreSQL password
$env:PGPASSWORD = "Mamlaka2020"

# Database parameters
$dbHost = "localhost"
$dbPort = "5432"
$dbName = "prizma"
$dbUser = "postgres"
$backupFile = "C:\hamisho\project_csms\database\hakib"

Write-Host "Database: $dbName" -ForegroundColor Yellow
Write-Host "Output file: $backupFile" -ForegroundColor Yellow
Write-Host ""

# Create backup
Write-Host "Creating backup..." -ForegroundColor Cyan
try {
    # Run pg_dump
    $process = Start-Process -FilePath "pg_dump" `
        -ArgumentList "-U", $dbUser, "-h", $dbHost, "-p", $dbPort, "-d", $dbName, "-f", $backupFile `
        -NoNewWindow -Wait -PassThru
    
    if ($process.ExitCode -eq 0) {
        Write-Host "SUCCESS: Backup created successfully!" -ForegroundColor Green
        
        # Get file info
        $fileInfo = Get-Item $backupFile
        Write-Host "File size: $($fileInfo.Length) bytes" -ForegroundColor Green
        Write-Host "Created: $($fileInfo.LastWriteTime)" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Backup failed with exit code $($process.ExitCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "To restore from this backup:" -ForegroundColor Yellow
Write-Host "psql -U postgres -d prizma -f hakib" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")