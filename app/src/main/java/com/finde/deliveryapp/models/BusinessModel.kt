package com.finde.deliveryapp.models

data class BusinessModel (
	val id : Int,
	val title : String,
	val description : String,
	val address : String,
	val user : String,
	val category : String,
	val created_by : CreatedBy,
	val updated_by : UpdatedBy,
	val created_at : String,
	val updated_at : String,
	val opening_hours : List<OpeningHours>,
	val geolocation : Geolocation,
	val social_links : List<SocialLinks>,
	val posts : List<String>
)