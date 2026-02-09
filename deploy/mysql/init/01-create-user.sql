-- Create application user for Docker network access
CREATE USER IF NOT EXISTS 'reports'@'%' IDENTIFIED BY 'P@yV@nce@1234';

-- Give permissions on both DBs
GRANT ALL PRIVILEGES ON core_saas.* TO 'reports'@'%';
GRANT ALL PRIVILEGES ON erp.* TO 'reports'@'%';

FLUSH PRIVILEGES;
