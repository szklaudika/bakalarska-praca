CREATE TABLE certificates (
     id INT AUTO_INCREMENT PRIMARY KEY,
     section VARCHAR(50) NOT NULL,       -- Napr. 'LETECKÉ KVALIFIKACE', 'MEDICAL', atď.
     platform VARCHAR(50) NOT NULL,      -- Napr. 'Velká éra', 'Ultralighty', 'Větroně', 'Vrtulníky'
     certificate_type VARCHAR(100) NOT NULL,
     expiry_date VARCHAR(20),            -- Ukladá hodnotu z "platí do (datum alebo '-')"
     note VARCHAR(255)
 );