<?php
$servername = "localhost";
$username = "root"; // XAMPP default username
$password = ""; // Default is empty
$dbname = "zapisnik_db";

// Connecting to the database
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get JSON data from Android
$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data)) {
    // If it's a single certificate object
    $name = $conn->real_escape_string($data['name']);
    $expiry_date = $conn->real_escape_string($data['expiryDate']); // Changed key to 'expiryDate'

    $sql = "INSERT INTO certificates (name, expiry_date) VALUES ('$name', '$expiry_date')";

    if ($conn->query($sql) === TRUE) {
        // Log success
        error_log("Certificate added successfully with ID: " . $conn->insert_id);
        echo json_encode(["status" => "success", "id" => $conn->insert_id]);
    } else {
        // Log failure
        error_log("Failed to add certificate: " . $conn->error);
        echo json_encode(["status" => "error", "message" => "Failed to add certificate"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();
?>
