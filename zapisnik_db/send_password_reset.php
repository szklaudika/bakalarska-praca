<?php
header("Content-Type: application/json");

// DB Connection
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

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit();
}

// ✅ Get email from form data (not JSON!)
$email = isset($_POST['email']) ? $_POST['email'] : '';

if (empty($email)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Email is required"]);
    exit();
}

// Check if email exists
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Email not found"]);
    exit();
}

$user = $result->fetch_assoc();
$userId = $user['id'];

// Generate secure token
$token = bin2hex(random_bytes(32));
$tokenExpiry = date('Y-m-d H:i:s', strtotime('+1 hour'));

// Store token
$updateStmt = $conn->prepare("UPDATE users SET reset_token = ?, reset_token_expiry = ? WHERE id = ?");
$updateStmt->bind_param("ssi", $token, $tokenExpiry, $userId);
$updateStmt->execute();

// SendGrid API email
$sendgridApiKey = ""; 
$resetLink = "https://zapisnik-2b2a59a43d05.herokuapp.com/reset_password_form.php?token=$token";
$subject = "Password Reset Request";
$messageText = "Click the following link to reset your password:\n\n$resetLink\n\nThis link will expire in 1 hour.";

$emailData = [
    "personalizations" => [[
        "to" => [["email" => $email]],
        "subject" => $subject
    ]],
    "from" => ["email" => "klaudika.sz362@gmail.com"], 
    "content" => [[
        "type" => "text/plain",
        "value" => $messageText
    ]]
];

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, 'https://api.sendgrid.com/v3/mail/send');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($emailData));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Authorization: Bearer ' . $sendgridApiKey,
    'Content-Type: application/json'
]);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

// Handle response
if ($httpCode == 202) {
    echo json_encode(["status" => "success", "message" => "Reset link sent to your email"]);
} else {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Failed to send email",
        "debug" => $response
    ]);
}
?>
