package com.example.pharmacy.database

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.pharmacy.*
import com.example.pharmacy.models.Cart
import com.example.pharmacy.models.Drug
import com.example.pharmacy.models.Order
import com.example.pharmacy.models.User
import com.example.pharmacy.ui.activities.*
import com.example.pharmacy.ui.fragments.DashboardFragment
import com.example.pharmacy.ui.fragments.OrdersFragment
import com.example.pharmacy.ui.fragments.ProductsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    var test = 8

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        mFireStore.collection(Common.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Ошибка регистрации.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUsersRole() : Int {

        mFireStore.collection(Common.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val doc = document.toObject(User::class.java)!!.role
                Log.e("userRoleFire", doc.toString())
                test = doc
            } .addOnFailureListener { e ->
                test = 15
                Log.e("","Error while getting the user role.", e)
            }
        return test
    }

    fun getUserDetails(activity: Activity) {
        mFireStore.collection(Common.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Common.PREF,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Common.LOG_USER,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                }

            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {

        mFireStore.collection(Common.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {

                when (activity) {
                    is ProfileActivity -> {
                        activity.profileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Common.getFileExtension(
                activity,
                imageFileURI
            )
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())


                        when (activity) {
                            is ProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddDrugActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


    fun uploadProductDetails(activity: AddDrugActivity, productInfo: Drug) {

        mFireStore.collection(Common.DRUGS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.",
                    e
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Common.DRUGS)
            .whereEqualTo(Common.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List", document.documents.toString())

                val drugList: ArrayList<Drug> = ArrayList()

                for (i in document.documents) {

                    val drug = i.toObject(Drug::class.java)
                    drug!!.product_id = i.id

                    drugList.add(drug)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(drugList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Product List", "Error while getting product list.", e)
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {

        mFireStore.collection(Common.DRUGS)
            .get()
            .addOnSuccessListener { document ->

                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                val productsList: ArrayList<Drug> = ArrayList()

                for (i in document.documents) {

                    val product = i.toObject(Drug::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                fragment.showDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFireStore.collection(Common.DRUGS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    fun getProductDetails(activity: DrugDetailsActivity, productId: String) {

        mFireStore.collection(Common.DRUGS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.toString())

                val product = document.toObject(Drug::class.java)!!

                activity.productDetailsSuccess(product)

            }
            .addOnFailureListener { e ->


                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    fun addCartItems(activity: DrugDetailsActivity, addToCart: Cart) {

        mFireStore.collection(Common.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: DrugDetailsActivity, productId: String) {

        mFireStore.collection(Common.CART_ITEMS)
            .whereEqualTo(Common.USER_ID, getCurrentUserID())
            .whereEqualTo(Common.DRUG_ID, productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFireStore.collection(Common.CART_ITEMS)
            .whereEqualTo(Common.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val list: ArrayList<Cart> = ArrayList()

                for (i in document.documents) {

                    val cartItem = i.toObject(Cart::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }
    fun getAllProductsList(activity: Activity) {
        mFireStore.collection(Common.DRUGS)
            .get()
            .addOnSuccessListener { document ->

                Log.e("Products List", document.documents.toString())

                val productsList: ArrayList<Drug> = ArrayList()

                for (i in document.documents) {

                    val product = i.toObject(Drug::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }

                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Get Product List", "Error while getting all product list.", e)
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {

        mFireStore.collection(Common.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        mFireStore.collection(Common.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }
    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Common.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
                )
            }
    }
    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Common.ORDERS)
            .whereEqualTo(Common.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<Order> = ArrayList()

                for (i in document.documents) {

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id

                    list.add(orderItem)
                }
                fragment.showOrders(list)
            }
            .addOnFailureListener { e ->
                Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
            }
    }
}