# CSMS Database Backup and Restoration

This directory contains complete database backup and restoration files for the Civil Service Management System (CSMS).

## Files Overview

| File | Purpose | Description |
|------|---------|-------------|
| `schema-backup.sql` | Schema Creation | Complete PostgreSQL schema with tables, indexes, and triggers |
| `csms-data-export.sql` | Data Import | All 1,044 records in SQL INSERT format |
| `csms-data-export.json` | Data Backup | All data in JSON format (alternative backup) |
| `backup-data.js` | Export Script | Node.js script to generate data exports using Prisma |
| `restore-database.bat` | Windows Restore | Automated restoration script for Windows |
| `restore-database.sh` | Linux Restore | Automated restoration script for Linux/Unix |
| `.env.example` | Configuration | Environment variable template |
| `MIGRATION_GUIDE.md` | Documentation | Complete migration instructions |

## Quick Start

### For Windows:
```cmd
restore-database.bat
```

### For Linux:
```bash
chmod +x restore-database.sh
./restore-database.sh
```

## Database Summary

- **Database Name**: `prizma`
- **Total Records**: 1,044
- **Tables**: 14
- **Generated**: July 22, 2025

### Data Breakdown:
- **Institutions**: 42 records
- **Users**: 162 records  
- **Employees**: 174 records
- **Employee Certificates**: 413 records
- **Confirmation Requests**: 18 records
- **Promotion Requests**: 26 records
- **LWOP Requests**: 12 records
- **Cadre Change Requests**: 13 records
- **Retirement Requests**: 17 records
- **Resignation Requests**: 14 records
- **Service Extension Requests**: 8 records
- **Separation Requests**: 10 records
- **Complaints**: 21 records
- **Notifications**: 114 records

## Requirements

- PostgreSQL 15+
- Node.js 18+ (for data export script)
- 10GB free disk space
- Administrative privileges

## Need Help?

1. Read the detailed [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
2. Check the [troubleshooting section](MIGRATION_GUIDE.md#troubleshooting)
3. Verify your [prerequisites](MIGRATION_GUIDE.md#prerequisites)

## Important Notes

- Default database credentials: `postgres:Mamlaka2020`
- Database name must be `prizma` (not `csms_db`)
- Ensure PostgreSQL service is running before restoration
- All scripts include data verification steps