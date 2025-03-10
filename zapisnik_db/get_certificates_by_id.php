<?php
header("Content-Type: application/json");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "zapisnik_db";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

// Check if the 'id' parameter is provided in the URL
if (isset($_GET['id'])) {
    $id = $_GET['id']; // Get the certificate ID from the URL

    // Log the incoming request for debugging
    error_log("Received request to fetch certificate with ID: $id");

    // Prepare the SQL query to get the certificate by ID
    $sql = "SELECT * FROM certificates WHERE id = ?";
    $stmt = $conn->prepare($sql);

    if ($stmt) {
        $stmt->bind_param("i", $id);  // Bind the ID parameter as an integer
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            // Fetch the certificate data
            $certificate = $result->fetch_assoc();
            echo json_encode($certificate);  // Return the certificate as JSON
        } else {
            echo json_encode(["error" => "Certificate not found"]);
        }

        $stmt->close();
    } else {
        echo json_encode(["error" => "Failed to prepare SQL statement"]);
    }
} else {
    echo json_encode(["error" => "ID parameter is missing"]);
}

$conn->close();
?>
