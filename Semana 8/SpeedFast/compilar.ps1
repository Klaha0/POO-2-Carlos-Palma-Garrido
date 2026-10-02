param(
    [switch]$Ejecutar
)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    & .\mvnw.cmd package
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion fallo.' }
    if ($Ejecutar) {
        & .\mvnw.cmd exec:java
        if ($LASTEXITCODE -ne 0) { throw 'La aplicacion finalizo con un error.' }
    }
} finally {
    Pop-Location
}
