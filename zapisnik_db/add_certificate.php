<?php
// Database connection parameters
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

// Retrieve JSON input from POST request
$data = json_decode(file_get_contents("php://input"), true);

// Extract values from the JSON object
$section = isset($data['section']) ? $data['section'] : '';
$platform = isset($data['platform']) ? $data['platform'] : '';
$certificate_type = isset($data['certificate_type']) ? $data['certificate_type'] : '';
$expiry_date = isset($data['expiry_date']) ? $data['expiry_date'] : '';
$note = isset($data['note']) ? $data['note'] : '';

// Use the same JSON key as in the flights code ("userId")
$user_id = isset($data['userId']) ? (int)$data['userId'] : 0;

// Prepare the SQL query for insertion, now including user_id
$stmt = $conn->prepare("INSERT INTO certificates (section, platform, certificate_type, expiry_date, note, user_id) VALUES (?, ?, ?, ?, ?, ?)");
if ($stmt === false) {
    die('Error preparing the SQL query: ' . $conn->error);
}

// Bind parameters: "sssss" for five strings and "i" for the user_id integer.
$stmt->bind_param("sssssi", $section, $platform, $certificate_type, $expiry_date, $note, $user_id);

// Execute the query
if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Certificate added successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => "Error: " . $stmt->error]);
}

// Close the statement and connection
$stmt->close();
$conn->close();
?>
