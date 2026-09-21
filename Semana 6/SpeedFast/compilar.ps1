param(
    [string]$IdeaHome = $env:IDEA_HOME,
    [switch]$Probar,
    [switch]$Ejecutar
)
$ErrorActionPreference = 'Stop'
if (-not $IdeaHome) {
    $instalacion = Get-ChildItem -Path "$env:LOCALAPPDATA\Programs" -Directory -Filter 'IntelliJ IDEA*' |
        Sort-Object Name -Descending | Select-Object -First 1
    if ($instalacion) { $IdeaHome = $instalacion.FullName }
}
if (-not $IdeaHome -or -not (Test-Path -LiteralPath "$IdeaHome\lib\forms_rt.jar")) {
    throw 'Indica la carpeta de IntelliJ IDEA con -IdeaHome o con la variable IDEA_HOME.'
}
$antLibraries = Join-Path $IdeaHome 'plugins\gradle-plugin\lib\ant\*'
$target = if ($Probar) { 'test' } else { 'compile' }
Push-Location $PSScriptRoot
try {
    & java -cp $antLibraries org.apache.tools.ant.Main "-Didea.home=$IdeaHome" $target
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion o las pruebas fallaron.' }
    if ($Ejecutar) {
        & java -cp "out\production\SpeedFast;$IdeaHome\lib\forms_rt.jar" main.Main
        if ($LASTEXITCODE -ne 0) { throw 'La aplicacion finalizo con un error.' }
    }
} finally {
    Pop-Location
}
