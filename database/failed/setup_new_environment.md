# Complete VPS Setup Guide for CSMS

This guide provides step-by-step instructions for setting up the Civil Service Management System on a new VPS from scratch.

## System Requirements

- **OS:** Ubuntu 20.04 LTS or later / CentOS 8+ / Windows Server 2019+
- **RAM:** Minimum 4GB, Recommended 8GB+
- **Storage:** Minimum 50GB SSD
- **CPU:** Minimum 2 cores, Recommended 4+ cores

## Step 1: Initial System Setup

### Ubuntu/Debian
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install essential tools
sudo apt install -y curl wget git vim htop unzip software-properties-common

# Create application user
sudo useradd -m -s /bin/bash csms
sudo usermod -aG sudo csms
```

### CentOS/RHEL
```bash
# Update system
sudo yum update -y

# Install essential tools
sudo yum install -y curl wget git vim htop unzip

# Create application user
sudo useradd -m -s /bin/bash csms
sudo usermod -aG wheel csms
```

## Step 2: Install PostgreSQL 15

### Ubuntu/Debian
```bash
# Add PostgreSQL official repository
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt update

# Install PostgreSQL 15
sudo apt install -y postgresql-15 postgresql-client-15

# Start and enable service
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### CentOS/RHEL
```bash
# Install PostgreSQL repository
sudo yum install -y https://download.postgresql.org/pub/repos/yum/reporpms/EL-8-x86_64/pgdg-redhat-repo-latest.noarch.rpm

# Install PostgreSQL 15
sudo yum install -y postgresql15-server postgresql15

# Initialize database
sudo /usr/pgsql-15/bin/postgresql-15-setup initdb

# Start and enable service
sudo systemctl start postgresql-15
sudo systemctl enable postgresql-15
```

### Configure PostgreSQL
```bash
# Switch to postgres user
sudo su - postgres

# Create database and user
createdb prizma
psql -c "ALTER USER postgres PASSWORD 'password123';"

# Exit postgres user
exit

# Configure PostgreSQL (Ubuntu/Debian path)
sudo vim /etc/postgresql/15/main/postgresql.conf
# Or for CentOS/RHEL
sudo vim /var/lib/pgsql/15/data/postgresql.conf

# Update these settings:
listen_addresses = '*'
port = 5432
max_connections = 100

# Configure authentication
sudo vim /etc/postgresql/15/main/pg_hba.conf
# Or for CentOS/RHEL
sudo vim /var/lib/pgsql/15/data/pg_hba.conf

# Add/modify these lines:
local   all             postgres                                md5
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5

# Restart PostgreSQL
sudo systemctl restart postgresql
```

## Step 3: Install Node.js 18+

```bash
# Install Node.js via NodeSource
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# Or for CentOS/RHEL
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs

# Verify installation
node --version  # Should be 18.x or higher
npm --version
```

## Step 4: Install Java 17

### Ubuntu/Debian
```bash
sudo apt install -y openjdk-17-jdk

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### CentOS/RHEL
```bash
sudo yum install -y java-17-openjdk-devel

# Set JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >> ~/.bashrc
source ~/.bashrc
```

## Step 5: Install Maven

```bash
# Download and install Maven
wget https://archive.apache.org/dist/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
sudo tar xzf apache-maven-3.9.4-bin.tar.gz -C /opt
sudo ln -s /opt/apache-maven-3.9.4 /opt/maven

# Add to PATH
echo 'export MAVEN_HOME=/opt/maven' >> ~/.bashrc
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify installation
mvn -version
```

## Step 6: Deploy Application

### Clone Repository (if available)
```bash
# Switch to csms user
sudo su - csms
cd /home/csms

# Clone repository
git clone <your-repository-url> project_csms
cd project_csms
```

### Or Extract from Backup
```bash
# Switch to csms user
sudo su - csms
cd /home/csms

# Copy your backup files to the server
# Extract if compressed
unzip csms_backup.zip  # or tar -xzf csms_backup.tar.gz
cd project_csms
```

## Step 7: Restore Database

```bash
cd /home/csms/project_csms/database

# Set permissions for scripts
chmod +x restore_database.sh

# Run restoration script
./restore_database.sh

# Or manually restore
export PGPASSWORD=password123
createdb -U postgres prizma
psql -U postgres -d prizma -f csms_complete.sql
```

## Step 8: Configure Application

### Frontend Configuration
```bash
cd /home/csms/project_csms/frontend

# Copy environment configuration
cp ../database/database_config.env .env

# Install dependencies
npm install

# Generate Prisma client
npx prisma generate
```

### Backend Configuration
```bash
cd /home/csms/project_csms/backend

# Install dependencies and build
mvn clean install
```

## Step 9: Setup System Services

### Frontend Service
```bash
sudo vim /etc/systemd/system/csms-frontend.service
```

```ini
[Unit]
Description=CSMS Frontend Service
After=network.target

[Service]
Type=simple
User=csms
WorkingDirectory=/home/csms/project_csms/frontend
Environment=NODE_ENV=production
Environment=PORT=9002
ExecStart=/usr/bin/npm run start
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Backend Service
```bash
sudo vim /etc/systemd/system/csms-backend.service
```

```ini
[Unit]
Description=CSMS Backend Service
After=network.target postgresql.service

[Service]
Type=simple
User=csms
WorkingDirectory=/home/csms/project_csms/backend
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=SPRING_PROFILES_ACTIVE=prod
ExecStart=/usr/bin/java -jar target/csms-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Enable and Start Services
```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable services
sudo systemctl enable csms-backend
sudo systemctl enable csms-frontend

# Start services
sudo systemctl start csms-backend
sudo systemctl start csms-frontend

# Check status
sudo systemctl status csms-backend
sudo systemctl status csms-frontend
```

## Step 10: Configure Firewall

### UFW (Ubuntu/Debian)
```bash
sudo ufw enable
sudo ufw allow ssh
sudo ufw allow 8080/tcp  # Backend
sudo ufw allow 9002/tcp  # Frontend
sudo ufw allow 5432/tcp  # PostgreSQL (if remote access needed)
sudo ufw status
```

### Firewalld (CentOS/RHEL)
```bash
sudo systemctl start firewalld
sudo systemctl enable firewalld
sudo firewall-cmd --permanent --add-service=ssh
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=9002/tcp
sudo firewall-cmd --permanent --add-port=5432/tcp
sudo firewall-cmd --reload
```

## Step 11: Setup Reverse Proxy (Optional)

### Install Nginx
```bash
# Ubuntu/Debian
sudo apt install -y nginx

# CentOS/RHEL
sudo yum install -y nginx

# Start and enable
sudo systemctl start nginx
sudo systemctl enable nginx
```

### Configure Nginx
```bash
sudo vim /etc/nginx/sites-available/csms
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Frontend
    location / {
        proxy_pass http://localhost:9002;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # Backend API
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # File uploads
    client_max_body_size 2M;
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/csms /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Step 12: Setup SSL (Optional)

### Using Certbot
```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Get SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

## Step 13: Monitoring and Logging

### Setup Log Rotation
```bash
sudo vim /etc/logrotate.d/csms
```

```
/home/csms/project_csms/backend/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    sharedscripts
    postrotate
        systemctl reload csms-backend
    endscript
}
```

### Monitor Services
```bash
# View logs
sudo journalctl -u csms-backend -f
sudo journalctl -u csms-frontend -f

# Check system resources
htop
df -h
```

## Step 14: Backup Strategy

### Create Backup Script
```bash
vim /home/csms/backup_csms.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/home/csms/backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Database backup
export PGPASSWORD=password123
pg_dump -U postgres -h localhost prizma > "$BACKUP_DIR/csms_db_$DATE.sql"

# Application backup
tar -czf "$BACKUP_DIR/csms_app_$DATE.tar.gz" -C /home/csms project_csms

# Keep only last 7 days
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
```

### Schedule Backups
```bash
chmod +x /home/csms/backup_csms.sh
crontab -e
# Add: 0 2 * * * /home/csms/backup_csms.sh
```

## Access Information

After completion, the system will be available at:
- **Frontend:** http://your-server-ip:9002 (or your-domain.com if using Nginx)
- **Backend API:** http://your-server-ip:8080/api
- **Database:** localhost:5432/prizma

### Default Login Credentials
- **Username:** admin
- **Password:** password123

## Troubleshooting

### Common Issues
1. **Port conflicts:** Ensure ports 8080, 9002, 5432 are available
2. **Memory issues:** Increase server RAM or configure swap
3. **Permission issues:** Ensure csms user owns all files
4. **Database connection:** Verify PostgreSQL is running and configured correctly
5. **Node.js version:** Ensure Node.js 18+ is installed

### Log Locations
- Backend: `/home/csms/project_csms/backend/logs/`
- Frontend: `sudo journalctl -u csms-frontend`
- PostgreSQL: `/var/log/postgresql/`
- Nginx: `/var/log/nginx/`

### Useful Commands
```bash
# Check service status
sudo systemctl status csms-backend csms-frontend postgresql nginx

# Restart services
sudo systemctl restart csms-backend csms-frontend

# Check disk space
df -h

# Monitor system resources
htop

# Check network connections
netstat -tlnp | grep :9002
netstat -tlnp | grep :8080
```

This completes the full VPS setup for the CSMS system.