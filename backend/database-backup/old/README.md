# CSMS Database Backup System

This directory contains comprehensive database backups and backup/restore scripts for the Civil Service Management System (CSMS) PostgreSQL database.

## Backup Files

### File Types:
- **`.sql`** - Plain SQL format (human-readable, largest size)
- **`.sql.gz`** - Compressed SQL format (smaller size, good for storage)
- **`.dump`** - PostgreSQL custom format (best for restoration, fastest)

### Naming Convention:
- Format: `csms_db_backup_YYYYMMDD_HHMMSS.extension`
- Example: `csms_db_backup_20250717_210747.dump`

## Scripts

### 1. backup_database.sh
**Purpose**: Creates comprehensive database backups

**Features**:
- Creates multiple backup formats (SQL, compressed, custom)
- Includes data-only and schema-only backups
- Automatic cleanup of old backups (keeps last 10)
- Timestamped backup files

**Usage**:
```bash
./backup_database.sh
```

### 2. restore_database.sh
**Purpose**: Restores database from backup files

**Features**:
- Supports all backup formats
- Safety confirmation before restoration
- Automatic database recreation
- Clear error messages

**Usage**:
```bash
./restore_database.sh [backup_file]
```

**Examples**:
```bash
./restore_database.sh csms_db_backup_20250717_210747.dump
./restore_database.sh csms_db_backup_20250717_210706.sql
./restore_database.sh csms_db_backup_20250717_210724.sql.gz
```

## Database Configuration

**Database**: `csms_db`  
**Host**: `localhost`  
**Port**: `5432`  
**User**: `postgres`  
**Password**: `Mamlaka2020`

## Manual Backup Commands

### Create SQL Backup:
```bash
PGPASSWORD=Mamlaka2020 pg_dump -h localhost -U postgres -d csms_db -f backup.sql
```

### Create Compressed Backup:
```bash
PGPASSWORD=Mamlaka2020 pg_dump -h localhost -U postgres -d csms_db | gzip > backup.sql.gz
```

### Create Custom Format Backup:
```bash
PGPASSWORD=Mamlaka2020 pg_dump -h localhost -U postgres -d csms_db -Fc -f backup.dump
```

## Manual Restore Commands

### Restore from SQL:
```bash
PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db -f backup.sql
```

### Restore from Compressed SQL:
```bash
gunzip -c backup.sql.gz | PGPASSWORD=Mamlaka2020 psql -h localhost -U postgres -d csms_db
```

### Restore from Custom Format:
```bash
PGPASSWORD=Mamlaka2020 pg_restore -h localhost -U postgres -d csms_db -v backup.dump
```

## Backup Schedule Recommendations

- **Daily**: Automated backups during low-usage hours
- **Weekly**: Full backup with retention
- **Monthly**: Archive backup for long-term storage
- **Before Updates**: Manual backup before system updates

## Security Notes

- Backup files may contain sensitive data
- Store backups in secure locations
- Consider encrypting backups for production use
- Regularly test restore procedures
- Monitor backup file sizes and integrity

## Troubleshooting

### Common Issues:

1. **Permission Denied**: Ensure scripts are executable (`chmod +x script.sh`)
2. **Database Connection**: Verify PostgreSQL is running and credentials are correct
3. **Disk Space**: Ensure sufficient space for backup files
4. **File Not Found**: Check backup file paths and names

### Logs:
Check PostgreSQL logs for detailed error messages:
```bash
sudo tail -f /var/log/postgresql/postgresql-*.log
```

## File Structure:
```
database-backup/
├── README.md                          # This file
├── backup_database.sh                 # Backup script
├── restore_database.sh                # Restore script
├── csms_db_backup_YYYYMMDD_HHMMSS.sql      # SQL backups
├── csms_db_backup_YYYYMMDD_HHMMSS.sql.gz   # Compressed backups
├── csms_db_backup_YYYYMMDD_HHMMSS.dump     # Custom format backups
├── csms_db_data_only_YYYYMMDD_HHMMSS.sql   # Data-only backups
└── csms_db_schema_only_YYYYMMDD_HHMMSS.sql # Schema-only backups
```

---
*Generated: $(date)*  
*CSMS Database Backup System v1.0*