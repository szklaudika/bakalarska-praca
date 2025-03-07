<?php
header("Content-Type: application/json");

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "zapisnik_db";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

// SQL query to select all flights
$sql = "SELECT * FROM flights"; // Assuming your table name is 'flights'
$result = $conn->query($sql);

// Initialize an array to hold the flight data
$flights = [];

// Fetch all records from the result
while ($row = $result->fetch_assoc()) {
    $flights[] = $row;
}

// Return the result as a JSON response
echo json_encode($flights);

// Close the database connection
$conn->close();
?>
