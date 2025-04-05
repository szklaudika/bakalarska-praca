<?php
header("Content-Type: application/json");

// Načítanie pripojovacích údajov: ak je nastavená premenná JAWSDB_URL (Heroku), použijeme ju, inak lokálne údaje.
$dbUrl = getenv('JAWSDB_URL');
if ($dbUrl) {
    $dbparts = parse_url($dbUrl);
    $servername = $dbparts['host'];
    $username = $dbparts['user'];
    $password = $dbparts['pass'];
    $dbname = ltrim($dbparts['path'], '/');
} else {
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "zapisnik_db";
}

// Vytvorenie pripojenia k databáze
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    error_log("Connection failed: " . $conn->connect_error);
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Načítanie JSON dát zo vstupu
$data = json_decode(file_get_contents("php://input"), true);
if (empty($data)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "No data received"]);
    exit();
}

// Extrahovanie hodnôt z JSON objektu
$section = isset($data['section']) ? $data['section'] : '';
$platform = isset($data['platform']) ? $data['platform'] : '';
$certificate_type = isset($data['certificate_type']) ? $data['certificate_type'] : '';
$expiry_date = isset($data['expiry_date']) ? $data['expiry_date'] : '';
$note = isset($data['note']) ? $data['note'] : '';
$user_id = isset($data['user_id']) ? (int)$data['user_id'] : 0;

// Pripravte SQL dotaz na vloženie certifikátu
$sql = "INSERT INTO certificates (section, platform, certificate_type, expiry_date, note, user_id)
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
if ($stmt === false) {
    http_response_code(500);
    error_log("Failed to prepare SQL query: " . $conn->error);
    echo json_encode(["status" => "error", "message" => "Failed to prepare SQL query: " . $conn->error]);
    exit();
}

// Viazanie parametrov: 5 reťazcov a 1 celé číslo ("sssssi")
$stmt->bind_param("sssssi", $section, $platform, $certificate_type, $expiry_date, $note, $user_id);

// Vykonanie dotazu
if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Certificate added successfully"]);
} else {
    http_response_code(500);
    error_log("Failed to add certificate: " . $stmt->error);
    echo json_encode(["status" => "error", "message" => "Failed to add certificate: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
