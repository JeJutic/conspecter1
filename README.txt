sudo apt-get install texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra
sudo apt-get install texlive-science
sudo apt-get install texlive-lang-cyrillic
sudo apt-get install postgresql
sudo -u postgres psql
\password postgres
...
CREATE DATABASE conspecter;
\c conspecter
// paste schema.sql
