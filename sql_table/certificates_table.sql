CREATE TABLE certificates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    section VARCHAR(50) NOT NULL,       -- Napr. 'LETECKÉ KVALIFIKACE', 'MEDICAL', atď.
    platform VARCHAR(50) NOT NULL,      -- Napr. 'Velká éra', 'Ultralighty', 'Větroně', 'Vrtulníky'
    certificate_type VARCHAR(100) NOT NULL,
    acquired_date VARCHAR(20),          -- Ukladá hodnotu z "získáno" (napr. '3/31/2025' alebo '-')
    expiry_date VARCHAR(20),            -- Ukladá hodnotu z "platí do (datum alebo '-')"
    days_remaining INT,                 -- Počet dní do uplynutia platnosti (môže byť aj záporný)
    note VARCHAR(255)
);
