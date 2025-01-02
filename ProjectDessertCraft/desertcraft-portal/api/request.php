<?php

header('Access-Control-Allow-Origin:*');
header('Content-Type: application/json');
header('Access-Control-Allow-Method: GET');
header('Access-Control-Allow-Method: POST');
header('Access-Control-Allow-Method: UPDATE');
header('Access-Control-Allow-Method: DELETE');
header('Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Request-With');

include('function.php');

if (isset($_GET['get_login_data'])) {

    $dataList = loginUser($conn);
} elseif (isset($_GET['set_signup_data'])) {

    $dataList = registerUser($conn);
} elseif (isset($_GET['get_desert_list'])) {

    $dataList = getDesertList($conn);
} elseif (isset($_GET['get_shop_location'])) {

    $dataList = getShopLocation($conn);
} elseif (isset($_GET['save_order'])) {

    $dataList = saveOrder($conn);
} elseif (isset($_GET['get_order_list'])) {

    $dataList = getMyOrderList($conn);
} elseif (isset($_GET['track_order'])) {

    $dataList = trackOrder($conn);
} elseif (isset($_GET['withdraw_order'])) {

    $dataList = withdrawOrder($conn);
} elseif (isset($_GET['save_rating'])) {

    $dataList = saveRating($conn);
} elseif (isset($_GET['get_promo_banner'])) {

    $dataList = getPromoBanner($conn);
} elseif (isset($_GET['get_points'])) {

    $dataList = getPoints($conn);
} elseif (isset($_GET['get_profile'])) {

    $dataList = getProfile($conn);
} elseif (isset($_GET['get_product_ratings'])) {

    $dataList = getProductRatings($conn);
} elseif (isset($_GET['forward_order_with_receipt'])) {

    $dataList = updateOrderStatusWithReceipt($conn);
} elseif (isset($_GET['update_profile'])) {

    $dataList = updateProfile($conn);
} elseif (isset($_GET['update_password'])) {

    $dataList = updatePassword($conn);
}

// ADMIN

elseif (isset($_GET['create_category'])) {

    $dataList = createCategory($conn);
} elseif (isset($_GET['get_category'])) {

    $dataList = getCategoryList($conn);
} elseif (isset($_GET['upload_product'])) {

    $dataList = uploadProduct($conn);
} elseif (isset($_GET['get_product_list'])) {

    $dataList = getProductList($conn);
} elseif (isset($_GET['get_product'])) {

    $dataList = getProduct($conn);
} elseif (isset($_GET['update_product'])) {

    $dataList = updateProduct($conn);
} elseif (isset($_GET['delete_product'])) {

    $dataList = deleteProduct($conn);
} elseif (isset($_GET['get_all_orders'])) {

    $dataList = getAllOrderList($conn);
} elseif (isset($_GET['get_orders'])) {

    $dataList = getOrders($conn);
} elseif (isset($_GET['forward_order'])) {

    $dataList = updateOrderStatus($conn);
} elseif (isset($_GET['get_counts'])) {

    $dataList = getStatusCount($conn);
} elseif (isset($_GET['upload_shop'])) {

    $dataList = uploadShop($conn);
} elseif (isset($_GET['update_shop'])) {

    $dataList = updateShop($conn);
} elseif (isset($_GET['update_banner'])) {

    $dataList = updateBanner($conn);
} elseif (isset($_GET['get_ratings'])) {

    $dataList = getRatings($conn);
} elseif (isset($_GET['get_insights'])) {

    $dataList = getInsight($conn);
}

echo $dataList;
