<?php
$servername = "localhost";
$username = "root"; // Change this if needed
$password = ""; // Default is empty for XAMPP
$dbname = "zapisnik_db";

// Use the correct variables here
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $usernameOrEmail = trim($_POST['usernameOrEmail']);
    $passwordInput = $_POST['password'];

    // Find user by username or email
    $stmt = $conn->prepare("SELECT id, username, email, password FROM users WHERE username = ? OR email = ?");
    if(!$stmt){
        echo json_encode(['success' => false, 'message' => 'Database error.']);
        exit;
    }
    $stmt->bind_param("ss", $usernameOrEmail, $usernameOrEmail);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        // Verify the password
        if (password_verify($passwordInput, $user['password'])) {
            // Here you can start a session or generate an authentication token
            echo json_encode(['success' => true, 'message' => 'Login successful', 'user' => $user]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Invalid password']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'User not found']);
    }
    $stmt->close();
    $conn->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request']);
}
?>
