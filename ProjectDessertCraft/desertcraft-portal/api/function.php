<?php

require '../config/server.php';


function generateUniqueUserId()
{
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';

    do {
        $randomString = '';
        for ($i = 0; $i < 15; $i++) {
            $randomString .= $characters[rand(0, strlen($characters) - 1)];
        }
    } while (uidExists($randomString));

    return $randomString;
}

function uidExists($uid)
{
    global $conn;
    $query = "SELECT email FROM users WHERE uid=?";
    $stmt = mysqli_prepare($conn, $query);
    mysqli_stmt_bind_param($stmt, 's', $uid);
    mysqli_stmt_execute($stmt);
    mysqli_stmt_store_result($stmt);
    $numRows = mysqli_stmt_num_rows($stmt);
    mysqli_stmt_close($stmt);

    return $numRows > 0;
}




function registerUser($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $name = $conn->real_escape_string($_POST['name']);
        $email = $conn->real_escape_string($_POST['email']);
        $dob = $conn->real_escape_string($_POST['dob']);
        $gender = $conn->real_escape_string($_POST['gender']);
        $mobile = $conn->real_escape_string($_POST['mobile']);
        $password = $conn->real_escape_string($_POST['password']);

        $checkQuery = "SELECT email FROM users WHERE email='$email'";
        $checkResult = $conn->query($checkQuery);

        if ($checkResult->num_rows > 0) {
            $data = [
                'success' => false,
                'message' => "USER ALREADY EXIST!"
            ];
        } else {
            $hashPass = password_hash($password, PASSWORD_DEFAULT);

            $userUid = generateUniqueUserId();

            $sqlQuery = "INSERT INTO `users`(`uid`, `name`, `email`, `dob`, `gender`, `mobile`, `password`) VALUES ('$userUid','$name','$email','$dob','$gender','$mobile','$hashPass')";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "SIGNUP SUCCESSFUL!"
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "SIGNUP FAILED! TRY AGAIN!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function loginUser($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $email = $conn->real_escape_string($_POST['email']);
        $password = $conn->real_escape_string($_POST['password']);

        $sqlQuery = "SELECT * FROM `users` WHERE `email` = '$email'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $user = $sqlResult->fetch_assoc();

            if (password_verify($password, $user['password'])) {
                $data = [
                    'success' => true,
                    'message' => "LOGIN SUCCESSFUL!",
                    'data' => $user
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "INCORRECT PASSWORD!"
                ];
            }
        } else {
            $data = [
                'success' => false,
                'message' => "USER NOT FOUND!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getDesertList($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT p.*, COALESCE(AVG(r.rating), 0) AS rating
                     FROM products p
                     LEFT JOIN ratings r ON p.pid = r.pid
                     GROUP BY p.pid";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $desertList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $desertList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO DESSERT AVAILABLE!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    echo json_encode($data);
}


function getShopLocation($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT * FROM `shop`";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows == 1) {
            $shop = $sqlResult->fetch_assoc();

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $shop
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO GET SHOP LOCATION!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function saveOrder($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        // Sanitize and escape input
        $uid = $conn->real_escape_string($_POST['uid']);
        $did = $conn->real_escape_string($_POST['did']);
        $image = $conn->real_escape_string($_POST['image']);
        $name = $conn->real_escape_string($_POST['desert_name']);
        $desc = $conn->real_escape_string($_POST['description']);
        $custom = $conn->real_escape_string($_POST['customization']);
        $price = $conn->real_escape_string($_POST['price']);
        $qty = $conn->real_escape_string($_POST['quantity']);
        $date = $conn->real_escape_string($_POST['date']);
        $inst = $conn->real_escape_string($_POST['instruction']);
        $pointUsed = $conn->real_escape_string($_POST['pointUsed']);
        $pointUsedInt = intval($pointUsed);

        $img_name = null; // Initialize img_name as null

        if (isset($_FILES['design']) && $_FILES['design']['error'] === UPLOAD_ERR_OK) {
            $upload_directory = "../uploads/";
            $extn = pathinfo($_FILES['design']['name'], PATHINFO_EXTENSION);
            $img_name = rand(1, 99999) . '-' . time() . '.' . $extn;
            $destination = $upload_directory . $img_name;

            if (!move_uploaded_file($_FILES['design']['tmp_name'], $destination)) {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPLOAD PRODUCT IMAGE!"
                ];
                $conn->close();
                return json_encode($data);
            }
        }

        // Build SQL query based on whether image was uploaded or not
        if ($img_name !== null) {
            $sqlQuery = "INSERT INTO `orders`(`desert_id`, `uid`, `image`, `desert_name`, `description`, `customization`, `price`, `quantity`, `on_date`, `instruction`, `design`) 
                        VALUES ('$did','$uid','$image','$name','$desc','$custom','$price','$qty','$date','$inst','$img_name')";
        } else {
            $sqlQuery = "INSERT INTO `orders`(`desert_id`, `uid`, `image`, `desert_name`, `description`, `customization`, `price`, `quantity`, `on_date`, `instruction`) 
                        VALUES ('$did','$uid','$image','$name','$desc','$custom','$price','$qty','$date','$inst')";
        }

        if ($conn->query($sqlQuery) === TRUE) {
            $checkQuery = "SELECT `points` FROM `rewards` WHERE `uid` = '$uid'";
            $checkResult = $conn->query($checkQuery);

            if ($checkResult->num_rows > 0) {
                $row = $checkResult->fetch_assoc();
                $currentPoints = (int)$row['points'];
                $updatedPoints = $currentPoints - $pointUsedInt;

                $updatePointsQuery = "UPDATE `rewards` SET `points` = '$updatedPoints' WHERE `uid` = '$uid'";
                if ($conn->query($updatePointsQuery) === TRUE) {
                    $data = [
                        'success' => true,
                        'message' => "ORDER SAVED SUCCESSFULLY"
                    ];
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO UPDATE POINTS!"
                    ];
                }
            } else {
                $data = [
                    'success' => true,
                    'message' => "ORDER SAVED SUCCESSFULLY BUT FAILED TO FIND USER POINTS!"
                ];
            }
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO SAVE ORDER!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}



function getMyOrderList($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $uid = $conn->real_escape_string($_POST['uid']);

        $sqlQuery = "SELECT * FROM `orders` WHERE `uid`='$uid' ORDER BY `id` DESC";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $orderList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $orderList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO GET ORDER LIST!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function trackOrder($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $id = $conn->real_escape_string($_POST['item_id']);

        $sqlQuery = "SELECT o.*, 
                            COALESCE(r.rating, '') AS rating, 
                            COALESCE(r.comments, '') AS comments 
                     FROM orders o
                     LEFT JOIN ratings r ON o.id = r.order_id
                     WHERE o.id = '$id'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $trackData = $sqlResult->fetch_assoc();

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $trackData
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO ORDER FOUND!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function withdrawOrder($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $uid = $conn->real_escape_string($_POST['uid']);
        $oid = $conn->real_escape_string($_POST['orderId']);

        $sqlQuery = "DELETE FROM `orders` WHERE `id` = '$oid' AND `uid` = '$uid'";


        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult === TRUE) {

            $data = [
                'success' => true,
                'message' => "ORDER WITHDRAWN!"
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO WITHDRAW! TRY AGAIN!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function saveRating($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $orderId = $conn->real_escape_string($_POST['order_id']);
        $desertId = $conn->real_escape_string($_POST['desert_id']);
        $rating = $conn->real_escape_string($_POST['rating']);
        $comments = $conn->real_escape_string($_POST['comments']);

        $sqlQuery = "INSERT INTO `ratings`(`order_id`, `pid`, `rating`, `comments`) VALUES ('$orderId','$desertId','$rating','$comments')";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult === TRUE) {
            $data = [
                'success' => true,
                'message' => "Rated!"
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "Error!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getPromoBanner($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT * FROM `banner`";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $desertList = $sqlResult->fetch_assoc();

            $data = [
                'success' => true,
                'data' => $desertList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "DESSERT CATEGORY NOT FOUND!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function getPoints($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $uid = $conn->real_escape_string($_POST['uid']);

        $sqlQuery = "SELECT * FROM `rewards` WHERE `uid`='$uid'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $points = $sqlResult->fetch_assoc();

            $data = [
                'success' => true,
                'data' => $points
            ];
        } else {
            $data = [
                'success' => true,
                'message' => "NO POINTS!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function updateProfile($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        // Sanitize and escape input
        $uid = $conn->real_escape_string($_POST['uid']);
        $name = $conn->real_escape_string($_POST['name']);
        $email = $conn->real_escape_string($_POST['email']);
        $mobile = $conn->real_escape_string($_POST['mobile']);

        $img_name = null; // Initialize img_name as null

        if (isset($_FILES['image']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
            $upload_directory = "../uploads/";
            $extn = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
            $img_name = rand(1, 99999) . '-' . time() . '.' . $extn;
            $destination = $upload_directory . $img_name;

            if (!move_uploaded_file($_FILES['image']['tmp_name'], $destination)) {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPLOAD PRODUCT IMAGE!"
                ];
                $conn->close();
                return json_encode($data);
            }
        }

        // Build SQL query based on whether image was uploaded or not
        if ($img_name !== null) {
            $sqlQuery = "UPDATE `users` SET `name`='$name',`email`='$email',`mobile`='$mobile',`image`='$img_name' WHERE `uid`='$uid'";
        } else {
            $sqlQuery = "UPDATE `users` SET `name`='$name',`email`='$email',`mobile`='$mobile' WHERE `uid`='$uid'";
        }

        if ($conn->query($sqlQuery) === TRUE) {
            $data = [
                'success' => true,
                'message' => "UPDATED SUCCESSFULLY"
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO SAVE!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function updatePassword($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        // Sanitize and escape input
        $uid = $conn->real_escape_string($_POST['uid']);
        $email = $conn->real_escape_string($_POST['email']);
        $currentPassword = $conn->real_escape_string($_POST['currentPassword']);
        $newPassword = $conn->real_escape_string($_POST['newPassword']);

        $sqlQuery = "SELECT * FROM `users` WHERE `email` = '$email'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $user = $sqlResult->fetch_assoc();

            if (password_verify($currentPassword, $user['password'])) {
                $hashPass = password_hash($newPassword, PASSWORD_DEFAULT);

                $sqlQuery = "UPDATE `users` SET `password`='$hashPass' WHERE `uid`='$uid'";
                $sqlResult = $conn->query($sqlQuery);

                if ($sqlResult === TRUE) {
                    $data = [
                        'success' => true,
                        'message' => "PASSWORD UPDATED!"
                    ];
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED! TRY AGAIN!"
                    ];
                }
            } else {
                $data = [
                    'success' => false,
                    'message' => "CURRENT PASSWORD INCORRECT!"
                ];
            }
        } else {
            $data = [
                'success' => false,
                'message' => "USER NOT FOUND!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}




// ADMIN PART
function createCategory($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $category = $conn->real_escape_string($_POST['category']);

        $checkQuery = "SELECT * FROM `category` WHERE `c_name`='$category'";
        $checkResult = $conn->query($checkQuery);

        if ($checkResult->num_rows > 0) {
            $data = [
                'success' => false,
                'message' => "CATEGORY ALREADY EXIST!"
            ];
        } else {

            $sqlQuery = "INSERT INTO `category`(`c_name`) VALUES ('$category')";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "CATEGORY ADDED SUCCESSFULLY!"
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "ERROR OCCURRED WHILE ADDING!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getCategoryList($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT * FROM `category`";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $catList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $catList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO CATEGORY FOUND!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function uploadProduct($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_FILES['image']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
        $name = $conn->real_escape_string($_POST['name']);
        $description = $conn->real_escape_string($_POST['description']);
        $category = $conn->real_escape_string($_POST['category']);
        $variation = $conn->real_escape_string($_POST['variation']);
        $price = $conn->real_escape_string($_POST['price']);
        $basePrice = $conn->real_escape_string($_POST['base_price']);
        $offerPrice = $conn->real_escape_string($_POST['offer_price']);

        $upload_directory = "../uploads/";

        if (!is_dir($upload_directory)) {
            mkdir($upload_directory, 0777, true);
        }

        $extn = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
        $img_name = rand(1, 99999) . '-' . time() . '.' . $extn;
        $destination = $upload_directory . $img_name;

        if (move_uploaded_file($_FILES['image']['tmp_name'], $destination)) {

            $sqlQuery = "INSERT INTO `products`(`cid`, `p_name`, `description`, `variation`, `price`, `p_image`, `base_price`, `offer_price`)
                                VALUES ('$category','$name','$description','$variation','$price','$img_name','$basePrice','$offerPrice')";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "PRODUCT ADDED SUCCESSFULLY"
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO ADD PRODUCT!"
                ];
            }
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO ADD PRODUCT!"
            ];
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getProductList($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT p.*, IFNULL(SUM(o.quantity), 0) AS total_sold
                        FROM products p
                        LEFT JOIN orders o ON p.pid = o.desert_id AND o.order_status IN (1, 2, 3, 4, 5)
                        GROUP BY p.pid";

        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $desertList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $desertList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO DESSERT AVAILABLE!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function getProduct($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $pid = $conn->real_escape_string($_POST['pid']);

        $sqlQuery = "SELECT * FROM `products` WHERE `pid`='$pid'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows == 1) {
            $wordList = $sqlResult->fetch_assoc();

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $wordList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "DESSERT NOT FOUND! TRY AGAIN!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function updateProduct($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $pid = $conn->real_escape_string($_POST['pid']);
        $name = $conn->real_escape_string($_POST['name']);
        $description = $conn->real_escape_string($_POST['description']);
        $category = $conn->real_escape_string($_POST['category']);
        $variation = $conn->real_escape_string($_POST['variation']);
        $price = $conn->real_escape_string($_POST['price']);
        $basePrice = $conn->real_escape_string($_POST['base_price']);
        $offerPrice = $conn->real_escape_string($_POST['offer_price']);

        if (isset($_FILES['image'])) {

            $upload_directory = "../uploads/";

            $extn = pathinfo($_FILES['image']['name'], PATHINFO_EXTENSION);
            $img_name = rand(1, 99999) . '-' . time() . '.' . $extn;
            $destination = $upload_directory . $img_name;

            $sqlSelect = "SELECT `p_image` FROM `products` WHERE `pid` = '$pid'";
            $result = $conn->query($sqlSelect);

            if ($result->num_rows > 0) {
                $row = $result->fetch_assoc();
                $current_image = $row['image'];

                if (!empty($current_image) && file_exists($upload_directory . $current_image)) {
                    unlink($upload_directory . $current_image);
                }
            }

            if (move_uploaded_file($_FILES['image']['tmp_name'], $destination)) {
                $sqlQuery = "UPDATE `products` SET `cid`='$category',`p_name`='$name',`description`='$description',`variation`='$variation',`price`='$price',`p_image`='$img_name',`base_price`='$basePrice',`offer_price`='$offerPrice' WHERE `pid`='$pid'";
                $sqlResult = $conn->query($sqlQuery);

                if ($sqlResult === TRUE) {
                    $data = [
                        'success' => true,
                        'message' => "PRODUCT UPDATED SUCCESSFULLY"
                    ];
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO UPDATE DESSERT!"
                    ];
                }
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE DESSERT!"
                ];
            }
        } else {
            $sqlQuery = "UPDATE `products` SET `cid`='$category',`p_name`='$name',`description`='$description',`variation`='$variation',`price`='$price',`base_price`='$basePrice',`offer_price`='$offerPrice' WHERE `pid`='$pid'";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "PRODUCT UPDATED SUCCESSFULLY"
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE DESSERT!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function deleteProduct($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $pid = $conn->real_escape_string($_POST['pid']);

        $sqlQuery = "DELETE FROM `products` WHERE `pid`='$pid'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult === TRUE) {

            $data = [
                'success' => true,
                'message' => "PRODUCT DELETED!"
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO DELETE! TRY AGAIN!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function uploadShop($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $name = $conn->real_escape_string($_POST['name']);
        $location = $conn->real_escape_string($_POST['location']);

        if (isset($_FILES['image'])) {
            $file = $_FILES['image'];
            $file_name = $file['name'];
            $file_tmp = $file['tmp_name'];

            $upload_directory = "../uploads/";
            $target_file = $upload_directory . basename($file_name);

            if (move_uploaded_file($file_tmp, $target_file)) {
                $sqlQuery = "INSERT INTO `shop`(`name`, `location`, `image`) VALUES ('$name','$location','$file_name')";
                $sqlResult = $conn->query($sqlQuery);

                if ($sqlResult === TRUE) {
                    $data = [
                        'success' => true,
                        'message' => "SHOP ADDED SUCCESSFULLY"
                    ];
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO ADD SHOP!"
                    ];
                }
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO ADD SHOP!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function updateShop($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $name = $conn->real_escape_string($_POST['name']);
        $location = $conn->real_escape_string($_POST['location']);
        $id = $conn->real_escape_string($_POST['id']);

        if (isset($_FILES['image'])) {
            $file = $_FILES['image'];
            $file_name = $file['name'];
            $file_tmp = $file['tmp_name'];

            $upload_directory = "../uploads/";
            $target_file = $upload_directory . basename($file_name);

            $sqlSelect = "SELECT `image` FROM `shop` WHERE `id` = '$id'";
            $result = $conn->query($sqlSelect);

            if ($result->num_rows > 0) {
                $row = $result->fetch_assoc();
                $current_image = $row['image'];

                // Delete the old image if it exists
                if (!empty($current_image) && file_exists($upload_directory . $current_image)) {
                    unlink($upload_directory . $current_image);
                }
            }

            if (move_uploaded_file($file_tmp, $target_file)) {
                $sqlQuery = "UPDATE `shop` SET `name` = '$name', `location` = '$location', `image` = '$file_name' WHERE `id` = '$id'";
                $sqlResult = $conn->query($sqlQuery);

                if ($sqlResult === TRUE) {
                    $data = [
                        'success' => true,
                        'message' => "DESERT UPDATED SUCCESSFULLY"
                    ];
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO UPDATE DESSERT!"
                    ];
                }
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE DESSERT!"
                ];
            }
        } else {
            $sqlQuery = "UPDATE `shop` SET `name` = '$name', `location` = '$location' WHERE `id` = '$id'";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "DESERT UPDATED SUCCESSFULLY"
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE DESSERT!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function updateBanner($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        if (isset($_FILES['image'])) {
            $file = $_FILES['image'];
            $file_name = $file['name'];
            $file_tmp = $file['tmp_name'];

            $upload_directory = "../uploads/";
            $target_file = $upload_directory . basename($file_name);

            $sqlSelect = "SELECT `image` FROM `banner` WHERE `id` = 1";
            $result = $conn->query($sqlSelect);

            if ($result->num_rows > 0) {
                $row = $result->fetch_assoc();
                $current_image = $row['image'];

                // Delete the old image if it exists
                if (!empty($current_image) && file_exists($upload_directory . $current_image)) {
                    unlink($upload_directory . $current_image);
                }
                if (move_uploaded_file($file_tmp, $target_file)) {
                    $sqlQuery = "UPDATE `banner` SET `image` = '$file_name' WHERE `id` = 1";
                    $sqlResult = $conn->query($sqlQuery);

                    if ($sqlResult === TRUE) {
                        $data = [
                            'success' => true,
                            'message' => "BANNER UPDATED SUCCESSFULLY"
                        ];
                    } else {
                        $data = [
                            'success' => false,
                            'message' => "FAILED TO UPDATE BANNER!"
                        ];
                    }
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO UPDATE BANNER!"
                    ];
                }
            } else {
                if (move_uploaded_file($file_tmp, $target_file)) {
                    $sqlQuery = "INSERT INTO `banner`(`image`) VALUES ('$file_name')";
                    $sqlResult = $conn->query($sqlQuery);

                    if ($sqlResult === TRUE) {
                        $data = [
                            'success' => true,
                            'message' => "BANNER ADDED SUCCESSFULLY"
                        ];
                    } else {
                        $data = [
                            'success' => false,
                            'message' => "FAILED TO ADD BANNER!"
                        ];
                    }
                } else {
                    $data = [
                        'success' => false,
                        'message' => "FAILED TO ADD BANNER!"
                    ];
                }
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getAllOrderList($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT * FROM `orders`";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $orderList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $orderList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO GET ORDERS LIST!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function getOrders($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $status = $conn->real_escape_string($_POST['status']);

        $sqlQuery = "SELECT * FROM `orders` WHERE `tracking_status`='$status'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult !== FALSE) {
            if ($sqlResult->num_rows > 0) {
                $orderList = $sqlResult->fetch_all(MYSQLI_ASSOC);

                $data = [
                    'success' => true,
                    'message' => "DATA RECEIVED!",
                    'data' => $orderList
                ];
            } else {
                $data = [
                    'success' => true,
                    'message' => "NO PENDING ORDERS!"
                ];
            }
        } else {
            $data = [
                'success' => false,
                'message' => "FAILED TO GET ORDER STATUS!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function updateOrderStatus($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $addonPrice = $conn->real_escape_string($_POST['addonPrice']);
        $orderStatus = $conn->real_escape_string($_POST['order_status']);
        $trackingStatus = $conn->real_escape_string($_POST['tracking_status']);
        $orderId = $conn->real_escape_string($_POST['item_id']);
        $uid = $conn->real_escape_string($_POST['uid']);
        $newPoints = $conn->real_escape_string($_POST['newPoints']);

        if ($addonPrice != "") {

            $sqlQuery = "UPDATE `orders` SET `add_on_price` = '$addonPrice', `order_status` = '$orderStatus', `tracking_status` = '$trackingStatus' WHERE `id` = '$orderId'";
            $sqlResult = $conn->query($sqlQuery);

            if (
                $sqlResult === TRUE
            ) {
                if ($orderStatus == "5") {

                    $checkQuery = "SELECT `points` FROM `rewards` WHERE `uid` = '$uid'";
                    $checkResult = $conn->query($checkQuery);

                    if ($checkResult->num_rows > 0) {
                        $row = $checkResult->fetch_assoc();
                        $currentPoints = (int)$row['points'];
                        $updatedPoints = $currentPoints + $newPoints;
                        $updatePointsQuery = "UPDATE `rewards` SET `points` = '$updatedPoints' WHERE `uid` = '$uid'";
                        $conn->query($updatePointsQuery);
                    } else {
                        $insertPointsQuery = "INSERT INTO `rewards` (`uid`, `points`) VALUES ('$uid', '$newPoints')";
                        $conn->query($insertPointsQuery);
                    }
                }
                $data = [
                    'success' => true,
                    'message' => "ORDER STATUS UPDATED."
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE ORDER STATUS!"
                ];
            }
        } else {
            $sqlQuery = "UPDATE `orders` SET `order_status` = '$orderStatus', `tracking_status` = '$trackingStatus' WHERE `id` = '$orderId'";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                if ($orderStatus == "5") {
                    $checkQuery = "SELECT `points` FROM `rewards` WHERE `uid` = '$uid'";
                    $checkResult = $conn->query($checkQuery);

                    if ($checkResult->num_rows > 0) {
                        $row = $checkResult->fetch_assoc();
                        $currentPoints = (int)$row['points'];
                        $updatedPoints = $currentPoints + $newPoints;
                        $updatePointsQuery = "UPDATE `rewards` SET `points` = '$updatedPoints' WHERE `uid` = '$uid'";
                        $conn->query($updatePointsQuery);
                    } else {
                        $insertPointsQuery = "INSERT INTO `rewards` (`uid`, `points`) VALUES ('$uid', '$newPoints')";
                        $conn->query($insertPointsQuery);
                    }
                }
                $data = [
                    'success' => true,
                    'message' => "ORDER STATUS UPDATED."
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE ORDER STATUS!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function updateOrderStatusWithReceipt($conn)
{
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $orderStatus = $conn->real_escape_string($_POST['order_status']);
        $trackingStatus = $conn->real_escape_string($_POST['tracking_status']);
        $orderId = $conn->real_escape_string($_POST['item_id']);
        $uid = $conn->real_escape_string($_POST['uid']);

        $img_name = null;

        if (isset($_FILES['receipt']) && $_FILES['receipt']['error'] === UPLOAD_ERR_OK) {
            $upload_directory = "../uploads/";
            $extn = pathinfo($_FILES['receipt']['name'], PATHINFO_EXTENSION);
            $img_name = rand(1, 99999) . '-' . time() . '.' . $extn;
            $destination = $upload_directory . $img_name;

            if (!move_uploaded_file($_FILES['receipt']['tmp_name'], $destination)) {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPLOAD PRODUCT IMAGE!"
                ];
                $conn->close();
                return json_encode($data);
            }
        }

        if ($img_name !== null) {
            $sqlQuery = "UPDATE `orders` SET `order_status` = '$orderStatus', `tracking_status` = '$trackingStatus', `receipt` = '$img_name' WHERE `id` = '$orderId' AND `uid` = '$uid'";
            $sqlResult = $conn->query($sqlQuery);

            if ($sqlResult === TRUE) {
                $data = [
                    'success' => true,
                    'message' => "ORDER STATUS UPDATED."
                ];
            } else {
                $data = [
                    'success' => false,
                    'message' => "FAILED TO UPDATE ORDER STATUS!"
                ];
            }
        }
    } else {
        $data = [
            'success' => false,
            'message' => "INVALID REQUEST!"
        ];
    }

    $conn->close();
    return json_encode($data);
}


function get_count_result($con, $query)
{
    $r = mysqli_query($con, $query);
    if ($r) {
        $result = $r->fetch_row();
        return $result[0];
    }
    return 0;
}


function getStatusCount($conn)
{

    $counts = [];

    $counts['total_pending_approval'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=0");
    $counts['total_need_pay'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=1");
    $counts['total_accepted'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=2");
    $counts['total_in_kitchen'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=3");
    $counts['total_ready_pickup'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=4");
    $counts['total_completed'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.tracking_status=5");
    $counts['total_cancelled'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.order_status=6");
    $counts['total_review'] = get_count_result($conn, "SELECT COUNT(DISTINCT r.id) FROM ratings r");

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $data = [
            'success' => true,
            'counts' => $counts
        ];
    }

    $conn->close();
    return json_encode($data);
}


function getInsight($conn)
{
    $counts = [];

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $startDate = $conn->real_escape_string($_POST['startDate']);
        $endDate = $conn->real_escape_string($_POST['endDate']);

        if ($startDate && $endDate) {

            $counts['total_users'] = get_count_result($conn, "SELECT COUNT(DISTINCT u.id) FROM users u WHERE DATE(u.created_on) BETWEEN '$startDate' AND '$endDate'");
            $counts['total_orders'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE DATE(o.created_on) BETWEEN '$startDate' AND '$endDate'");
            $counts['total_sales'] = get_count_result($conn, "SELECT COUNT(DISTINCT s.id) FROM orders s WHERE DATE(s.created_on) BETWEEN '$startDate' AND '$endDate' AND s.tracking_status=5");

            $data = [
                'success' => true,
                'counts' => $counts
            ];
        } else {
            $data = [
                'success' => false,
                'message' => 'Invalid date values provided.'
            ];
        }

        $conn->close();
        return json_encode($data);
    }

    return json_encode(['success' => false, 'message' => 'Invalid request.']);
}


function getRatings($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'GET') {

        $sqlQuery = "SELECT * FROM `ratings`";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $catList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $catList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO CATEGORY FOUND!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function getProductRatings($conn)
{

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $pid = $conn->real_escape_string($_POST['pid']);

        $sqlQuery = "SELECT * FROM `ratings` WHERE `pid`='$pid'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $ratingList = $sqlResult->fetch_all(MYSQLI_ASSOC);

            $data = [
                'success' => true,
                'message' => "DATA RECEIVED!",
                'data' => $ratingList
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "NO CATEGORY FOUND!"
            ];
        }
    }

    $conn->close();
    return json_encode($data);
}


function getProfile($conn)
{
    $counts = [];

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {

        $uid = $conn->real_escape_string($_POST['uid']);

        $sqlQuery = "SELECT * FROM `users` WHERE `uid` = '$uid'";
        $sqlResult = $conn->query($sqlQuery);

        if ($sqlResult->num_rows > 0) {
            $user = $sqlResult->fetch_assoc();

            $counts['total_orders'] = get_count_result($conn, "SELECT COUNT(DISTINCT o.id) FROM orders o WHERE o.uid='$uid'");

            $data = [
                'success' => true,
                'message' => "SUCCESSFUL!",
                'data' => $user,
                'count' => $counts
            ];
        } else {
            $data = [
                'success' => false,
                'message' => "USER NOT FOUND!"
            ];
        }

        $conn->close();
        return json_encode($data);
    }

    return json_encode(['success' => false, 'message' => 'Invalid request.']);
}
