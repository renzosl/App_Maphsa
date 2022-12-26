package com.benlinux.realestatemanager.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.benlinux.realestatemanager.R
import com.benlinux.realestatemanager.data.userManager.UserManager
import com.benlinux.realestatemanager.injections.ViewModelFactory
import com.benlinux.realestatemanager.ui.adapters.PictureAdapter
import com.benlinux.realestatemanager.ui.models.Picture
import com.benlinux.realestatemanager.ui.models.Property
import com.benlinux.realestatemanager.ui.models.PropertyAddress
import com.benlinux.realestatemanager.ui.models.User
import com.benlinux.realestatemanager.utils.*
import com.benlinux.realestatemanager.viewmodels.PropertyViewModel
import com.google.android.material.textfield.TextInputLayout

class UpdatePropertyActivity: AppCompatActivity() {

    lateinit var property: Property
    private var propertyId: String? = null

    // Property type radio buttons
    private lateinit var typeRadioGroup1: RadioGroup
    private lateinit var typeRadioGroup2: RadioGroup

    // The viewModel that contains data
    private lateinit var propertyViewModel: PropertyViewModel


    // Input fields & layouts
    private lateinit var titleLayout: TextInputLayout
    private lateinit var title: EditText
    private lateinit var areaLayout: TextInputLayout
    private lateinit var area: EditText
    private lateinit var priceLayout: TextInputLayout
    private lateinit var price: EditText
    private lateinit var surfaceLayout: TextInputLayout
    private lateinit var surface: EditText
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var description: EditText
    private lateinit var emptyRecyclerViewText: TextView
    private lateinit var addressTitle: TextView
    private lateinit var streetNumberLayout: TextInputLayout
    private lateinit var streetNumber: EditText
    private lateinit var streetNameLayout: TextInputLayout
    private lateinit var streetName: EditText
    private lateinit var addressComplementLayout: TextInputLayout
    private lateinit var addressComplement: EditText
    private lateinit var postalCodeLayout: TextInputLayout
    private lateinit var postalCode: EditText
    private lateinit var cityLayout: TextInputLayout
    private lateinit var city: EditText
    private lateinit var countryLayout: TextInputLayout
    private lateinit var country: EditText

    // The recycler view, the list + the adapter for pictures gallery
    private lateinit var picturesRecyclerView: RecyclerView
    private lateinit var picturesList: MutableList<Picture?>
    private lateinit var pictureAdapter: PictureAdapter

    // Spinners
    private lateinit var roomSpinner: Spinner
    private lateinit var bedroomSpinner: Spinner
    private lateinit var bathroomSpinner: Spinner

    // Save button
    private lateinit var saveButton: Button

    // Realtor Data
    private lateinit var realtor: User



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_proprerty)

        retrievePropertyId()
        setToolbar()
        setTypeRadioButtons()
        setViewModel()
        setViews()
        setPicturesGallery()
        getCurrentRealtor()
        setListenerOnUpdateButton()










    }

    private fun getCurrentRealtor() {
        UserManager.getUserData()?.addOnSuccessListener { user ->
            if (user != null) {
                realtor = User(
                    user.id, user.email, user.firstName, user.lastName,
                    user.avatarUrl, user.favorites, user.isRealtor, user.realtorProperties
                )
            }
        }
    }

    // Pictures gallery configuration
    private fun setPicturesGallery() {
        // Define layout & adapter
        picturesList = mutableListOf()
        picturesRecyclerView = findViewById(R.id.add_pictures_list)
        pictureAdapter = PictureAdapter(picturesList, this )
        picturesRecyclerView.adapter = pictureAdapter
    }

    // Retrieve property's pictures and add each of them in pictures list
    private fun retrievePropertyPictures(property: Property) {
        for (picture: Picture? in property.pictures) {
            picturesList.add(picture)
            pictureAdapter.notifyItemInserted(picturesList.lastIndex)
        }
    }

    // Toolbar configuration
    private fun setToolbar() {
        val mToolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(mToolbar)
        mToolbar.title = resources.getString(R.string.add_property_activity_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    // Set radio buttons for property type
    private fun setTypeRadioButtons() {
        typeRadioGroup1 = findViewById(R.id.add_type_radioGroup1)
        typeRadioGroup2 = findViewById(R.id.add_type_radioGroup2)
        checkIfPropertyTypeIsChecked(typeRadioGroup1, typeRadioGroup2)
    }

    // Retrieve property id
    private fun retrievePropertyId() {
        propertyId = intent.extras?.getString("PROPERTY_ID")
        Log.d("ID", propertyId.toString())
    }

    // Set all activity views
    private fun setViews() {
        titleLayout = findViewById(R.id.add_name_layout)
        title = findViewById(R.id.add_name_input)
        title.requestFocus()
        areaLayout = findViewById(R.id.add_area_layout)
        area = findViewById(R.id.add_area_input)
        priceLayout = findViewById(R.id.add_price_layout)
        price = findViewById(R.id.add_price_input)
        surfaceLayout = findViewById(R.id.add_surface_layout)
        surface = findViewById(R.id.add_surface_input)
        descriptionLayout = findViewById(R.id.add_description_layout)
        description = findViewById(R.id.add_description_text)
        picturesRecyclerView = findViewById(R.id.add_pictures_list)
        emptyRecyclerViewText = findViewById(R.id.empty_error_text)
        addressTitle = findViewById(R.id.add_address_layout_title)
        streetNumberLayout = findViewById(R.id.add_street_number_layout)
        streetNumber = findViewById(R.id.add_street_number_input)
        streetNameLayout = findViewById(R.id.add_street_name_layout)
        streetName = findViewById(R.id.add_street_name_input)
        addressComplementLayout = findViewById(R.id.add_address_complement_layout)
        addressComplement = findViewById(R.id.add_address_complement_input)
        postalCodeLayout = findViewById(R.id.add_postal_code_layout)
        postalCode = findViewById(R.id.add_postal_code_input)
        cityLayout = findViewById(R.id.add_city_layout)
        city = findViewById(R.id.add_city_input)
        countryLayout = findViewById(R.id.add_country_layout)
        country = findViewById(R.id.add_country_input)
        saveButton = findViewById(R.id.create)
    }


    private fun setData(property: Property) {
        fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
        fun Int.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this.toString())
        title.text = property.name.toEditable()
        area.text = property.area.toEditable()
        price.text = property.price.toEditable()
        surface.text = property.surface.toEditable()
        description.text = property.description.toEditable()
        streetNumber.text = property.address.streetNumber.toEditable()
        streetName.text = property.address.streetName.toEditable()
        addressComplement.text = property.address.complement.toEditable()
        postalCode.text = property.address.postalCode.toEditable()
        city.text = property.address.city.toEditable()
        country.text = property.address.country.toEditable()
        setRoomsSpinners(property.numberOfRooms)
        setBedroomsSpinners(property.numberOfBedrooms)
        setBathroomsSpinners(property.numberOfBathrooms)
    }



    // Configuring ViewModel from ViewModelFactory
    private fun setViewModel() {
        // Define ViewModel
        val viewModelFactory = ViewModelFactory(this.application)
        propertyViewModel = ViewModelProvider(this, viewModelFactory)[PropertyViewModel::class.java]

        propertyViewModel.getPropertyById(propertyId!!.toInt() )
        // Set observer on current property
        propertyViewModel.currentProperty?.observe(this) { actualProperty ->
            property = actualProperty
            setData(property)
            retrievePropertyPictures(property)
        }

    }

    // Rooms number spinner
    private fun setRoomsSpinners(numberOfRooms: Int) {

        roomSpinner = findViewById(R.id.add_number_rooms_count)
        val roomsItems = arrayListOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25, 26,27,28,29,30)
        val roomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roomsItems)
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roomSpinner.adapter = roomAdapter
        roomSpinner.setSelection(numberOfRooms)
        roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedText = parent!!.getChildAt(0) as TextView
                selectedText.setTextColor(
                    ContextCompat.getColor(applicationContext, R.color.colorAccent))
                property.numberOfRooms = roomAdapter.getItem(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    // Bedrooms number spinner
    private fun setBedroomsSpinners(numberOfBedrooms: Int) {
        bedroomSpinner = findViewById(R.id.add_number_bedrooms_count)
        val bedroomsItems = arrayListOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val bedroomAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            bedroomsItems
        )
        bedroomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bedroomSpinner.adapter = bedroomAdapter
        bedroomSpinner.setSelection(numberOfBedrooms)
        bedroomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedText = parent!!.getChildAt(0) as TextView
                selectedText.setTextColor(
                    ContextCompat.getColor(applicationContext, R.color.colorAccent))
                property.numberOfBedrooms = bedroomAdapter.getItem(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    // Bathrooms number spinner
    private fun setBathroomsSpinners(numberOfBathrooms: Int) {
        // Bathrooms spinner
        bathroomSpinner = findViewById(R.id.add_number_bathrooms_count)
        val bathroomsItems = arrayListOf(0,1,2,3,4,5,6,7,8,9,10)
        val bathroomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bathroomsItems)
        bathroomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bathroomSpinner.adapter = bathroomAdapter
        bathroomSpinner.setSelection(numberOfBathrooms)
        bathroomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedText = parent!!.getChildAt(0) as TextView
                selectedText.setTextColor(
                    ContextCompat.getColor(applicationContext, R.color.colorAccent))
                property.numberOfBathrooms = bathroomAdapter.getItem(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    /**
     * Close add property activity and turn back to main activity if back button is clicked
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val propertyDetailsActivityIntent = Intent(this, PropertyDetailsActivity::class.java)
            propertyDetailsActivityIntent.putExtra("PROPERTY_ID", propertyId)
            startActivity(propertyDetailsActivityIntent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Set save button actions
    private fun setListenerOnUpdateButton() {
        saveButton.text = resources.getString(R.string.update_button_text)
        saveButton.setOnClickListener {
            if (checkPropertyFields()) {
                updateProperty()
                propertyViewModel.updateProperty(property)
                Log.d("PROPERTY UPDATED", property.toString())
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
            } else {
                Log.d("PROPERTY NOT UPDATED", "Not validated")
            }
        }
    }

    // Fields validation
    private fun checkPropertyFields(): Boolean {
        return (checkIfFieldsIsNotEmpty(areaLayout)
                && validateNumbers(price.text.toString(), priceLayout)
                && validateNumbers(surface.text.toString(), surfaceLayout)
                && checkIfFieldsIsNotEmpty(descriptionLayout)
                && validateField(title.text.toString(), titleLayout)
                && validateNumbers(streetNumber.text.toString(), streetNumberLayout)
                && validateField(streetName.text.toString(), streetNameLayout)
                && validateNumbers(postalCode.text.toString(), postalCodeLayout)
                && validateField(city.text.toString(), cityLayout)
                && validateField(country.text.toString(), countryLayout))
    }

    // Create property action that retrieves all data
    private fun updateProperty() {
        property.name = title.text.toString()
        property.area = area.text.toString()
        property.type = getPropertyType()
        getPropertyPrice()
        getPropertySurface()
        property.description = description.text.toString()
        property.isAvailable = isPropertyAvailable()
        // property.creationDate = getTodayDate()
        property.pictures = picturesList
        property.realtor = realtor
        property.address = getPropertyAddress()
        property.id = propertyId!!.toInt()
    }

    private fun getPropertyType(): String {
        val selectedRadioButtonIDinGroup1: Int = typeRadioGroup1.checkedRadioButtonId
        val selectedRadioButtonIDinGroup2: Int = typeRadioGroup2.checkedRadioButtonId

        // If nothing is selected from Radio Group, then it return -1
        return if (selectedRadioButtonIDinGroup1 != -1) {
            val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonIDinGroup1)
            selectedRadioButton.text.toString()
        } else { // it means that button is checked in Group 2
            val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonIDinGroup2)
            selectedRadioButton.text.toString()
        }
    }

    private fun getPropertyPrice() {
        val priceValue = price.text.toString()
        try {
            property.price = priceValue.toInt()
        } catch (e: NumberFormatException) {
            Log.d("ERROR PRICE CONVERSION", e.toString())
        }
    }

    private fun getPropertySurface() {
        val surfaceValue = surface.text.toString()
        try {
            property.surface = surfaceValue.toInt()
        } catch (e: NumberFormatException) {
            Log.d("ERROR SURFACE", e.toString())
        }
    }

    // Get property availability
    private fun isPropertyAvailable(): Boolean {
        val available: RadioButton = findViewById(R.id.add_status_radioButton1)
        return available.isChecked
    }

    // Create property Address
    private fun getPropertyAddress(): PropertyAddress {
        return PropertyAddress(
            streetNumber.text.toString(),
            streetName.text.toString(),
            addressComplement.text.toString(),
            postalCode.text.toString(),
            city.text.toString(),
            country.text.toString()
        )
    }

}