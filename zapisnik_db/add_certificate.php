<?php
$servername = "localhost";
$username = "root"; // XAMPP default username
$password = ""; // Default je prázdne
$dbname = "zapisnik_db";

// Pripojenie na databázu
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Získanie JSON dát z Androidu
$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data)) {
    // If it's a single certificate object
    $name = $conn->real_escape_string($data['name']);
    $expiry_date = $conn->real_escape_string($data['expiryDate']); // Changed key to 'expiryDate'

    $sql = "INSERT INTO certificates (name, expiry_date) VALUES ('$name', '$expiry_date')";
    $conn->query($sql);

    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();

?>
