# Script pentru vizualizarea logurilor Docker în mod frumos
param(
    [string]$Service = "all",
    [int]$Lines = 50,
    [switch]$Follow
)

$workingDir = "d:\FACULTATE\MASTER1\sasps-project\sasps-project.rest-api"
Set-Location $workingDir

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   SASPS Project - Docker Logs Viewer  " -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

function Show-ServiceLogs {
    param(
        [string]$ServiceName,
        [string]$Color
    )
    
    Write-Host "`n--- Loguri $ServiceName ---`n" -ForegroundColor $Color
    
    if ($Follow) {
        docker-compose -f docker-compose.yaml logs --tail=$Lines -f $ServiceName
    } else {
        docker-compose -f docker-compose.yaml logs --tail=$Lines $ServiceName
    }
}

switch ($Service.ToLower()) {
    "frontend" {
        Show-ServiceLogs -ServiceName "frontend" -Color "Green"
    }
    "backend" {
        Show-ServiceLogs -ServiceName "backend" -Color "Blue"
    }
    "postgres" {
        Show-ServiceLogs -ServiceName "postgres" -Color "Yellow"
    }
    "all" {
        Write-Host "Servicii active:" -ForegroundColor Magenta
        docker-compose -f docker-compose.yaml ps
        
        Write-Host "`n`nLoguri recente de la toate serviciile:`n" -ForegroundColor Magenta
        
        if ($Follow) {
            docker-compose -f docker-compose.yaml logs --tail=$Lines -f
        } else {
            docker-compose -f docker-compose.yaml logs --tail=$Lines
        }
    }
    default {
        Write-Host "Serviciu necunoscut: $Service" -ForegroundColor Red
        Write-Host "Servicii disponibile: frontend, backend, postgres, all" -ForegroundColor Yellow
    }
}

Write-Host "`n========================================`n" -ForegroundColor Cyan
