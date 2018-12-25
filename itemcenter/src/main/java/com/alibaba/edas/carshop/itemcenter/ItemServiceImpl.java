package com.alibaba.edas.carshop.itemcenter;


/**
 * Alibaba Group EDAS. http://www.aliyun.com/product/edas
 */
public class ItemServiceImpl implements ItemService {

	@Override
	public Item getItemById( long id ) {
		Item car = new Item();
		car.setItemId( 1L );
		car.setItemName( "Mercedes Benz" );
		return car;
	}
	@Override
	public Item getItemByName( String name ) {
		Item car = new Item();
		car.setItemId( 1L );
		car.setItemName( "Mercedes Benz" );
		return car;
	}

	@Override
	public String callBackTest(String str) {
		System.out.println("1232483545");
		return "test";
	}
}
