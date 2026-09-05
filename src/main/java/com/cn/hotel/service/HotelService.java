package com.cn.hotel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.cn.hotel.communicator.RatingServiceCommunicator;
import com.cn.hotel.exceptions.HotelNotFoundException;
import com.cn.hotel.model.Hotel;

@Service
public class HotelService {

	List<Hotel> hotelList = new ArrayList<>();
	Map<String,Hotel> hotelMap= new HashMap<>();
	
	@Autowired
	RatingServiceCommunicator ratingServiceCommunicator;
	
	public void createHotel(Hotel hotel) {
		
		Map<String, Long> ratingsMap = new HashMap<>();
		hotelList.add(hotel);
		hotelMap.put(hotel.getId(), hotel);
		ratingsMap.put(hotel.getId(), hotel.getRating());
		ratingServiceCommunicator.addRating(ratingsMap);
	}


	public Hotel getHotelById(String id) {
		
		if(ObjectUtils.isEmpty(hotelMap.get(id)))
		{
			throw new HotelNotFoundException("Hotel not found for id: "+id);
		}
		Hotel hotel = hotelMap.get(id);
		//rest service to fetch the rating by id
		long updatedRating=ratingServiceCommunicator.getRating(id);
		hotel.setRating(updatedRating);
		return hotel;
	}


	public List<Hotel> getAllHotels() {
		
		return hotelList;
	}


	public void deleteHotelById(String id) {
		Hotel hotel = getHotelById(id);
		hotelList.remove(hotel);
		hotelMap.remove(id);
		ratingServiceCommunicator.deleteRating(id);
	}


	public void updateHotel(Hotel updatedHotel) {
		//1. Get the previous data of the hotel
		//2. remove this old data from list
		//3. Add the updated data to the list.
		
		Hotel existingHotel= getHotelById(updatedHotel.getId());
		
		hotelList.remove(existingHotel);
		hotelList.add(updatedHotel);
		
		
		
		//4. update the previous data with new data.
		//5. Update the map with new data.
		
		hotelMap.put(updatedHotel.getId(), updatedHotel);
		
		Map<String,Long> updatedRating = new HashMap<>();
		updatedRating.put(updatedHotel.getId(), updatedHotel.getRating());
		ratingServiceCommunicator.updateRating(updatedRating);
		
	}

	
	
}
