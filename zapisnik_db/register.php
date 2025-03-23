<?php
$servername = "localhost";
$username = "root"; // Change this if needed
$password = ""; // Default is empty for XAMPP
$dbname = "zapisnik_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get user input and perform necessary validation/sanitization
    $usernameInput = trim($_POST['username']);
    $email = trim($_POST['email']);
    $passwordInput = $_POST['password'];

    if(empty($usernameInput) || empty($email) || empty($passwordInput)){
        echo json_encode(['success' => false, 'message' => 'All fields are required.']);
        exit;
    }
    
    // Hash the password
    $hashedPassword = password_hash($passwordInput, PASSWORD_DEFAULT);

    // Prepare and execute the insert query
    $stmt = $conn->prepare("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
    if(!$stmt){
        echo json_encode(['success' => false, 'message' => 'Database error: ' . $conn->error]);
        exit;
    }
    $stmt->bind_param("sss", $usernameInput, $email, $hashedPassword);

    if ($stmt->execute()) {
        echo json_encode(['success' => true, 'message' => 'User registered successfully']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Registration failed: ' . $stmt->error]);
    }
    $stmt->close();
    $conn->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request']);
}
?>
