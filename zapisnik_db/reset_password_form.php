<?php
// PHP logic
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
if ($conn->connect_error) die("DB error");

$token = $_GET['token'] ?? '';
$message = '';

if (empty($token)) {
    $message = "Invalid token.";
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $newPassword = $_POST['password'] ?? '';
    if (strlen($newPassword) < 6) {
        $message = "Password must be at least 6 characters.";
    } else {
        $hashed = password_hash($newPassword, PASSWORD_DEFAULT);
        $stmt = $conn->prepare("UPDATE users SET password = ?, reset_token = NULL, reset_token_expiry = NULL WHERE reset_token = ? AND reset_token_expiry > NOW()");
        $stmt->bind_param("ss", $hashed, $token);
        $stmt->execute();
        if ($stmt->affected_rows > 0) {
            $message = "✅ Password reset successful! You can now log in.";
        } else {
            $message = "❌ Token invalid or expired.";
        }
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reset Password</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        body {
            background-color: #121212;
            color: #ffffff;
            font-family: 'Segoe UI', sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .container {
            background-color: #1e1e1e;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 0 20px rgba(255, 255, 255, 0.05);
            width: 90%;
            max-width: 400px;
            text-align: center;
        }

        h2 {
            margin-bottom: 20px;
        }

        input[type="password"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            border: none;
            border-radius: 8px;
            background-color: #2e2e2e;
            color: #fff;
            font-size: 16px;
        }

        button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            background-color: #e57373;
            color: #fff;
            font-size: 16px;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        button:hover {
            background-color: #ef5350;
        }

        .message {
            margin-top: 20px;
            font-size: 14px;
        }

        a {
            color: #f06292;
            text-decoration: none;
        }
        form {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 15px;
        }

        form input,
        form button {
        width: 100%;
        max-width: 300px; 
        }

    </style>
</head>
<body>
    <div class="container">
        <h2>Reset Password</h2>

        <?php if (!empty($message)): ?>
            <div class="message"><?= $message ?></div>
        <?php elseif (!empty($token)): ?>
            <form method="POST">
                <input type="password" name="password" placeholder="New password" required>
                <button type="submit">Reset Password</button>
            </form>
        <?php endif; ?>
    </div>
</body>
</html>
