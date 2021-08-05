# dependency env install
apt install -y zlib1g zlib1g.dev openssl vim build-essential libssl-dev && 

#install clamav
wget http://www.clamav.net/downloads/production/clamav-0.101.0.tar.gz &&
tar zxvf clamav-0.101.0.tar.gz && cd clamav-0.101.0;
./configure --prefix=/usr/local/clamav && (make && make install) && cp /usr/local/clamav/etc/clamd.conf.sample /usr/local/clamav/etc/clamd.conf;

# modify config file
sed -i '8s/Example/#Example/g' /usr/local/clamav/etc/clamd.conf && 
echo 'LogFile /usr/local/clamav/logs/clamd.log' >> /usr/local/clamav/etc/clamd.conf && 
echo 'PidFile /usr/local/clamav/updata/clamd.pid' >> /usr/local/clamav/etc/clamd.conf && 
echo 'DatabaseDirectory /usr/local/clamav/updata/clamav' >> /usr/local/clamav/etc/clamd.conf;

cp /usr/local/clamav/etc/freshclam.conf.sample /usr/local/clamav/etc/freshclam.conf &&
sed -i '8s/Example/#Example/g' /usr/local/clamav/etc/freshclam.conf && 
echo 'DatabaseDirectory /usr/local/clamav/updata' >> /usr/local/clamav/etc/freshclam.conf &&
echo 'UpdateLogFile /usr/local/clamav/logs/freshclam.log' >> /usr/local/clamav/etc/freshclam.conf && 
echo 'PidFile /usr/local/clamav/updata/freshclam.pid' >> /usr/local/clamav/etc/freshclam.conf;


# create user
groupadd clamav;
useradd -g clamav clamav;

# create dir file
mkdir /usr/local/clamav/logs &&
mkdir /usr/local/clamav/updata &&
touch /usr/local/clamav/logs/freshclam.log &&
chown clamav:clamav /usr/local/clamav/logs/freshclam.log &&
touch /usr/local/clamav/logs/clamd.log &&
chown clamav:clamav /usr/local/clamav/logs/clamd.log &&
chown clamav:clamav /usr/local/clamav/updata;

# update virus data
/usr/local/clamav/bin/freshclam;
