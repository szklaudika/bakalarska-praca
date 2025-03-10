<?php
// Database connection
$servername = "localhost";  // Change if needed
$username = "root";         // Your database username
$password = "";             // Your database password
$dbname = "zapisnik_db";    // Your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get data from POST request
$data = json_decode(file_get_contents("php://input"), true);
$name = $data['name'];
$expiry_date = $data['expiry_date'];

// Prepare the SQL query
$stmt = $conn->prepare("INSERT INTO certificates (name, expiry_date) VALUES (?, ?)");
if ($stmt === false) {
    die('Error preparing the SQL query: ' . $conn->error);
}

// Bind parameters (use "ss" for two string parameters)
$stmt->bind_param("ss", $name, $expiry_date);

// Execute the query
if ($stmt->execute()) {
    // Success response
    echo json_encode(["status" => "success", "message" => "Certificate added successfully"]);
} else {
    // Error response
    echo json_encode(["status" => "error", "message" => "Error: " . $stmt->error]);
}

// Close the statement and the connection
$stmt->close();
$conn->close();
?>
