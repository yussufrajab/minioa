@echo off
echo CSMS Manual Database Backup Commands
echo ====================================
echo.
echo Since automated backup is timing out, please run these commands manually:
echo.
echo 1. Open Command Prompt as Administrator
echo.
echo 2. Set the PostgreSQL password:
echo    set PGPASSWORD=Mamlaka2020
echo.
echo 3. Create complete backup (choose ONE of these methods):
echo.
echo    Method A - Complete backup:
echo    pg_dump -U postgres -h localhost -p 5432 prizma ^> csms_complete.sql
echo.
echo    Method B - Schema and data separately:
echo    pg_dump -U postgres -h localhost -p 5432 --schema-only prizma ^> csms_schema.sql
echo    pg_dump -U postgres -h localhost -p 5432 --data-only --inserts prizma ^> csms_data.sql
echo.
echo    Method C - Essential tables only:
echo    pg_dump -U postgres -h localhost -p 5432 -t "User" -t "Employee" -t "Institution" prizma ^> csms_essential.sql
echo.
echo 4. Verify backup was created:
echo    dir *.sql
echo.
echo 5. Test the backup (optional):
echo    createdb -U postgres test_prizma
echo    psql -U postgres -d test_prizma -f csms_complete.sql
echo    dropdb -U postgres test_prizma
echo.
echo ALTERNATIVE: Use pgAdmin GUI
echo 1. Open pgAdmin
echo 2. Connect to localhost with user: postgres, password: Mamlaka2020
echo 3. Right-click on 'prizma' database
echo 4. Select 'Backup...'
echo 5. Choose filename and format
echo 6. Click 'Backup'
echo.
pause