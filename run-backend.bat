@echo off
echo 🚀 Lancement du backend MAKEUPNOW avec variables d'environnement .env...
dotenv -e .env -- mvnw spring-boot:run

echo.
echo ⛔ Si le script s'est fermé, il y a peut-être une erreur au lancement.
pause
