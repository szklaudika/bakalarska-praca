<?php
// Database connection
$servername = "localhost";  // Change if needed
$username = "root";         // Your database username
$password = "";             // Your database password
$dbname = "zapisnik_db";    // Your database name

$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Check if required parameters are provided
if (isset($_GET['name']) && isset($_GET['expiry_date'])) {
    $name = $conn->real_escape_string($_GET['name']);
    $expiry_date = $conn->real_escape_string($_GET['expiry_date']);

    // Query to check if a certificate exists with the same name and expiry date
    $sql = "SELECT * FROM certificates WHERE name = '$name' AND expiry_date = '$expiry_date'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        // Certificate exists
        $certificate = $result->fetch_assoc();
        echo json_encode($certificate);
    } else {
        // Certificate does not exist
        http_response_code(404);
        echo json_encode(["message" => "Certificate not found"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["error" => "Missing required parameters"]);
}

// Close connection
$conn->close();
?>