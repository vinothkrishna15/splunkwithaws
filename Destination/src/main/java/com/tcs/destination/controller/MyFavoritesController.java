package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.MyFavorites;
import com.tcs.destination.service.MyFavoritesService;

@RestController
@RequestMapping("/favorites")
public class MyFavoritesController {

	@Autowired
	MyFavoritesService myFavService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody List<MyFavorites>  findOne(
			@PathVariable("id") String userId) {
		return myFavService.findFavoritesFor(userId);
	}
}
