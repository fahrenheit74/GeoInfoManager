package com.jhlabs.map;

public class ZoneBndy {
	
	public static double[][] quadcoord= new double[122][4];
	
	public static void fillquadcoord(int cnt, double lllat, double lllng, double urlat, double urlng){
		quadcoord[cnt][0]= lllat;
		quadcoord[cnt][1]= lllng;
		quadcoord[cnt][2]= urlat;
		quadcoord[cnt][3]= urlng;
	}
	public static void fillquadcoord2(int cnt, double lllng, double lllat, double urlng, double urlat){
		quadcoord[cnt][0]= lllat;
		quadcoord[cnt][1]= lllng;
		quadcoord[cnt][2]= urlat;
		quadcoord[cnt][3]= urlng;
	}
	
	public static int findsuggestedzoneNA(double lat, double lng){
		fillquadcoord(0,46.6000,-54.5000,49.8000,-52.6200);      //MTM Zone 1
		fillquadcoord(1,46.6000,-57.5000,54.7000,-54.5000);      //MTM Zone 2
		fillquadcoord(2,47.4000,-60.5000,54.7000,-57.5000);      //MTM Zone 3
		fillquadcoord(3,47.1300,-63.0000,59.1000,-60.0000);      //MTM Zone 4
		fillquadcoord(4,47.8800,-66.0000,60.5200,-63.0000);      //MTM Zone 5
		fillquadcoord(5,47.1000,-69.0000,59.1000,-66.0000);      //MTM Zone 6
		fillquadcoord(6,44.9900,-72.0000,61.9000,-69.0000);      //MTM Zone 7
		fillquadcoord(7,44.9900,-75.0000,62.5600,-72.0000);      //MTM Zone 8
		fillquadcoord(8,43.5000,-78.0000,62.5600,-75.0000);      //MTM Zone 9
		fillquadcoord(9,42.1000,-81.0000,62.4600,-78.0000);      //MTM Zone 10
		fillquadcoord(10,41.6800,-83.5900,46.0000,-81.0000);     //MTM Zone 11
		fillquadcoord(11,46.0000,-82.5000,55.5300,-79.5000);     //MTM Zone 12
		fillquadcoord(12,46.0000,-85.5000,55.5300,-82.5000);     //MTM Zone 13
		fillquadcoord(13,47.2000,-88.5000,56.7000,-85.5000);     //MTM Zone 14
		fillquadcoord(14,47.8000,-91.5000,56.8600,-88.5000);     //MTM Zone 15
		fillquadcoord(15,48.0000,-94.5000,55.3000,-91.5000);     //MTM Zone 16
		fillquadcoord(16,48.5000,-95.1500,55.3000,-94.5000);     //MTM Zone 17
		fillquadcoord(17,77.0000,-73.5000,79.0000,-72.0000);     //GR96 / UTM zone 18N
		fillquadcoord(18,76.0000,-72.0000,81.0000,-66.0000);     //GR96 / UTM zone 19N
		fillquadcoord(19,75.0000,-66.0000,82.0000,-60.0000);     //GR96 / UTM zone 20N
		fillquadcoord(20,59.0000,-60.0000,82.0000,-54.0000);     //GR96 / UTM zone 21N
		fillquadcoord(21,59.0000,-54.0000,83.0000,-48.0000);     //GR96 / UTM zone 22N
		fillquadcoord(22,59.0000,-48.0000,84.0000,-42.0000);     //GR96 / UTM zone 23N
		fillquadcoord(23,59.0000,-42.0000,84.0000,-36.0000);     //GR96 / UTM zone 24N
		fillquadcoord(24,59.0000,-36.0000,84.0000,-30.0000);     //GR96 / UTM zone 25N
		fillquadcoord(25,59.0000,-30.0000,84.0000,-24.0000);     //GR96 / UTM zone 26N
		fillquadcoord(26,59.0000,-24.0000,84.0000,-18.0000);     //GR96 / UTM zone 27N
		fillquadcoord(26,59.0000,-18.0000,84.0000,-12.0000);     //GR96 / UTM zone 27N
		
		for(int j= 0; j<17;j++){
			if((lat > quadcoord[j][0])&&(lat < quadcoord[j][2])){  
				if((lng > quadcoord[j][1])&&(lng < quadcoord[j][3])){
					return j;
				}
			}
		}
		for(int j= 17; j<27;j++){
			if((lat > quadcoord[j][0])&&(lat < quadcoord[j][2])){  
				if((lng > quadcoord[j][1])&&(lng < quadcoord[j][3])){
					return j+9;
				}
			}
		}
		
		return 0;
	}
	public static int findsuggestedzoneSA(double lat, double lng){
		if((lng > -84)&&(lng < -30)){
			if(lat > 0){
				if(lng < -78) return 0;
				return (int)(Math.floor((lng+78.0)/6));
			}
			else{
				return (int)(Math.floor((lng+84.0)/6)+5);
			}
		}
		return 0;
	}
	public static int findsuggestedzoneRU(double lat, double lng){
		return 0;
	}
	public static int findsuggestedzoneCH(double lat, double lng){
		fillquadcoord2(0,113.8900, 22.1600, 114.5700, 22.6200);        //Hong Kong 1980 Grid System
		fillquadcoord2(1,  73.6200, 35.4400, 78.0000, 41.0700);        //Xian 1980/Gauss-Kruger Zone 13 EPSG:2327
		fillquadcoord2(2,  78.0000, 29.1600, 84.0000, 47.2200);        //Xian 1980/Gauss-Kruger Zone 14 EPSG:2328
		fillquadcoord2(3,  84.0000, 27.3200, 90.0000, 49.1700);        //Xian 1980/Gauss-Kruger Zone 15 EPSG:2329
		fillquadcoord2(4,  90.0000, 27.7300, 96.0000, 47.8900);        //Xian 1980/Gauss-Kruger Zone 16 EPSG:2330
		fillquadcoord2(5,  96.0000, 21.1400, 102.0000, 43.1700);       //Xian 1980/Gauss-Kruger Zone 17 EPSG:2331
		fillquadcoord2(6, 102.0000, 21.5400, 108.0000, 42.4700);       //Xian 1980/Gauss-Kruger Zone 18 EPSG:2332
		fillquadcoord2(7, 108.0000, 18.1700, 114.0000, 45.1000);       //Xian 1980/Gauss-Kruger Zone 19 EPSG:2333
		fillquadcoord2(8, 114.0000, 22.1900, 120.0000, 51.5200);       //Xian 1980/Gauss-Kruger Zone 20 EPSG:2334
		fillquadcoord2(9, 120.0000, 21.9300, 126.0000, 53.5500);       //Xian 1980/Gauss-Kruger Zone 21 EPSG:2335
		fillquadcoord2(10,126.0000, 40.8900, 132.0000, 52.7800);       //Xian 1980/Gauss-Kruger Zone 22 EPSG:2336
		fillquadcoord2(11,132.0000, 45.0200, 134.7700, 48.3900);       //Xian 1980/Gauss-Kruger Zone 23 EPSG:2337
		fillquadcoord2(12,132.0000, 45.0200, 134.7700, 48.3900);       //Xian 1980/Gauss-Kruger Zone 23 EPSG:2337
		fillquadcoord2(13, 73.6200, 35.4400, 78.00000, 41.0700);       //Xian 1980/Gauss-Kruger CM 75E EPSG:2338
		fillquadcoord2(14,78.0000, 29.1600, 84.0000, 47.2200 );        //Xian 1980/Gauss-Kruger CM 81E EPSG:2339
		fillquadcoord2(16,84.0000, 27.3200, 90.0000, 49.1700 );        //Xian 1980/Gauss-Kruger CM 87E EPSG:2340
		fillquadcoord2(17,90.0000, 27.7300, 96.0000, 47.8900 );        //Xian 1980/Gauss-Kruger CM 93E EPSG:2341
		fillquadcoord2(18,96.0000, 21.1400, 102.000, 43.1700 );        //Xian 1980/Gauss-Kruger CM 99E EPSG:2342
		fillquadcoord2(19,102.0000, 21.5400, 108.0000, 42.4700 );      //Xian 1980/Gauss-Kruger CM 105E EPSG:2343
		fillquadcoord2(20,108.0000, 18.1700, 114.0000, 45.1000 );      //Xian 1980/Gauss-Kruger CM 111E EPSG:2344
		fillquadcoord2(21,114.0000, 22.1900, 120.0000, 51.5200 );      //Xian 1980/Gauss-Kruger CM 117E EPSG:2345
		fillquadcoord2(22,120.0000, 21.9300, 126.0000, 53.5500 );      //Xian 1980/Gauss-Kruger CM 123E EPSG:2346
		fillquadcoord2(23,126.0000, 40.8900, 132.0000, 52.7800 );      //Xian 1980/Gauss-Kruger CM 129E EPSG:2347
		fillquadcoord2(24,132.0000, 45.0200, 134.7700, 48.3900 );      //Xian 1980/Gauss-Kruger CM 135E EPSG:2348
		fillquadcoord2(25,73.6200, 35.8100, 76.5000, 40.6400 );      //Xian 1980 / 3-degree Gauss-Kruger zone 25 EPSG:2349
		fillquadcoord2(26,76.5000, 31.0000, 79.5000, 41.8300 );      //Xian 1980 / 3-degree Gauss-Kruger zone 26 EPSG:2350
		fillquadcoord2(27,79.5000, 29.9600, 82.5000, 45.8800 );      //Xian 1980 / 3-degree Gauss-Kruger zone 27 EPSG:2351
		fillquadcoord2(28,82.5000, 28.2600, 85.5000, 47.2200 );      //Xian 1980 / 3-degree Gauss-Kruger zone 28 EPSG:2352
		fillquadcoord2(29,85.5000, 27.8100, 88.5000, 49.1700 );      //Xian 1980 / 3-degree Gauss-Kruger zone 29 EPSG:2353
		fillquadcoord2(30,88.5000, 27.3200, 91.5000, 48.4100 );      //Xian 1980 / 3-degree Gauss-Kruger zone 30 EPSG:2354
		fillquadcoord2(31,91.5000, 27.7300, 94.5000, 45.1300 );      //Xian 1980 / 3-degree Gauss-Kruger zone 31 EPSG:2355
		fillquadcoord2(32,94.5000, 28.2200, 97.5000, 44.4900 );      //Xian 1980 / 3-degree Gauss-Kruger zone 32 EPSG:2356
		fillquadcoord2(33,97.5000, 21.4400, 100.5000, 42.7500 );     //Xian 1980 / 3-degree Gauss-Kruger zone 33 EPSG:2357
		fillquadcoord2(34,100.5000, 21.1400, 103.5000, 42.6900 );    //Xian 1980 / 3-degree Gauss-Kruger zone 34 EPSG:2358
		fillquadcoord2(35,103.5000, 22.5000, 106.5000, 42.200 );     //Xian 1980 / 3-degree Gauss-Kruger zone 35 EPSG:2359
		fillquadcoord2(36,106.5000, 18.2600, 109.5000, 42.4700 );    //Xian 1980 / 3-degree Gauss-Kruger zone 36 EPSG:2360
		fillquadcoord2(37,109.5000, 18.1700, 112.5000, 45.1000 );    //Xian 1980 / 3-degree Gauss-Kruger zone 37 EPSG:2361
		fillquadcoord2(38,112.5000, 21.5700, 115.5000, 45.4400 );    //Xian 1980 / 3-degree Gauss-Kruger zone 38 EPSG:2362
		fillquadcoord2(39,115.5000, 22.6600, 118.5000, 49.8800 );    //Xian 1980 / 3-degree Gauss-Kruger zone 39 EPSG:2363
		fillquadcoord2(40,118.5000, 21.9300, 121.5000, 53.3300 );    //Xian 1980 / 3-degree Gauss-Kruger zone 40 EPSG:2364
		fillquadcoord2(41,121.5000, 23.5000, 124.5000, 53.5500 );    //Xian 1980 / 3-degree Gauss-Kruger zone 41 EPSG:2365
		fillquadcoord2(42,124.5000, 40.2000, 127.5000, 53.2000 );    //Xian 1980 / 3-degree Gauss-Kruger zone 42 EPSG:2366
		fillquadcoord2(43,127.5000, 41.3700, 130.5000, 50.2500 );    //Xian 1980 / 3-degree Gauss-Kruger zone 43 EPSG:2367
		fillquadcoord2(44,130.5000, 42.4200, 133.5000, 48.8800  );    //Xian 1980 / 3-degree Gauss-Kruger zone 44 EPSG:2368
		fillquadcoord2(45,133.5000, 45.8600, 134.7700, 48.3900 );    //Xian 1980 / 3-degree Gauss-Kruger zone 45 EPSG:2369
		
		for(int j= 0; j<46;j++){
			if((lat > quadcoord[j][0])&&(lat < quadcoord[j][2])){  
				if((lng > quadcoord[j][1])&&(lng < quadcoord[j][3])){
					return j;
				}
			}
		}
		return 0;
		
		
	}
	
	
	public static int findsuggestedzoneEU(double lat, double lng){
		//ETRS TM only
		//Zone Range 30W - 48E
		//Lat constant 24N - 65.8N
		//Zones 26 - 51 
		
		if((lat > 24)&&(lat < 65.8)){  
			if((lng > -30)&&(lng < 48))
				return (int)(Math.floor((lng+30.0)/6)+1);
			else
				return 0;
		}
		else
			return 0;
	}
		
	public static int findsuggestedzone(double lat, double lng){
		
		fillquadcoord(0,31.25,-87.00,35.25,-84.75);      	//Alabama E
		fillquadcoord(1,30.00,-88.75,35.25,-86.25);       	//Alabama W
		fillquadcoord(2,54.0,-128.0,61.0,-141.0);         	//Alaska I
		fillquadcoord(3,59.0,-144.5,71.0,-140.5);         	//Alaska II
		fillquadcoord(4,59.0,-148.5,71.0,-143.5);         	//Alaska III
		fillquadcoord(5,58.5,-152.5,71.5,-147.5);         	//Alaska IV
		fillquadcoord(6,56.0,-156.5,71.75,-151.5);        	//Alaska V
		fillquadcoord(7,54.0,-160.5,71.75,-155.5);        	//Alaska VI
		fillquadcoord(8,54.0,-165.5,71.0,-159.5);         	//Alaska VII
		fillquadcoord(9,54.0,-169.5,70.0,-163.5);         	//Alaska VIII
		fillquadcoord(10,52,-173.75,66,-167.5);            	//Alaska IX
		fillquadcoord(11,50,176,55,-163.5);                	//Alaska X
		fillquadcoord(12,31.00,-112.00,37.25,-109.25);    	//Arizona E
		fillquadcoord(13,31.00,-113.75,37.25,-110.25);    	//Arizona C
		fillquadcoord(14,32.25,-115.25,37.25,-112.25);    	//Arizona W
		fillquadcoord(15,34.50,-95.00,36.75,-89.50);      	//Arkansas N
		fillquadcoord(16,32.75,-94.75,35.50,-90.00);      	//Arkansas S
		fillquadcoord(17,39.25,-124.5,42.5,-119.75);      	//California I
		fillquadcoord(18,37.50,-124.50,40.5,-119.25);     	//California II
		fillquadcoord(19,36.50,-123.25,39.0,-117.5);      	//California III
		fillquadcoord(20,35.5,-122.25,37.75,-115.5);      	//California IV
		fillquadcoord(21,32.5,-122,36.00,-113.75);          //California V
		fillquadcoord(22,32,-118.5,34.25,-114.25);          //California VI
		fillquadcoord(23,39.25,-109.25,41.5,-101.75);     	//Colorodo N
		fillquadcoord(24,37.75,-109.25,40.5,-101.75);     	//Colorodo C
		fillquadcoord(25,36.75,-109.25,39.0,-101.75);     	//Colorodo S
		fillquadcoord(26,40.75,-74.0,42.5,-71.5);         	//Connecticut
		fillquadcoord(27,38.0,-76,40,-74.5);                //Delaware
		fillquadcoord(28,24,-82.5,31,-79.5);              	//Florida E
		fillquadcoord(29,26,-84.0,29.75,-81);             	//Florida W
		fillquadcoord(30,29.0,-88.0,31.5,-81.75);         	//Florida N
		fillquadcoord(31,30,-83.75,35,-80.5);             	//Georgia E
		fillquadcoord(32,30.25,-86,35.25,-83);            	//Georgia W
		fillquadcoord(33,18,-157,21,-155.5);              	//Hawaii I
		fillquadcoord(34,20,-158,22,-155);                	//Hawaii II
		fillquadcoord(35,20.5,-159,22.5,-157);              //Hawaii III
		fillquadcoord(36,20.5,-160.5,23,-158.);             //Hawaii IV
		fillquadcoord(37,21,-161,23,-159);                  //Hawaii V
		fillquadcoord(38,41.5,-113.5,45,-111.5);          	//Idaho E
		fillquadcoord(39,41.5,-115.5,46,-113.5);          	//Idaho C
		fillquadcoord(40,41.5,-117.75,49.5,-114);         	//Idaho W
		fillquadcoord(41,36.75,-89.5,42.75,-86.75);         //Illinois E
		fillquadcoord(42,36.75,-91.5,42.75,-88.75);         //Illinois W
		fillquadcoord(43,37.5,-86.75,42.25,-84.5);          //Indiana E
		fillquadcoord(44,37.5,-88.5,42.25,-86.0);         	//Indiana W
		fillquadcoord(45,41.5,-97,43.75,-89.5);             //Iowa N
		fillquadcoord(46,40.25,-96.5,42.5,-89.);          	//Iowa S
		fillquadcoord(47,38.25,-102.5,40.5,-94.25);         //Kansas N
		fillquadcoord(48,36.75,-102.5,39,-94.25);         	//Kansas S
		fillquadcoord(49,37.5,-86.25,39.5,-82.25);          //Kentucky N
		fillquadcoord(50,36,-89.75,38.25,-81.5);          	//Kentucky S
		fillquadcoord(51,30.5,-94.5,33.25,-90.75);          //Louisiana N
		fillquadcoord(52,28.5,-94.25,31.25,-88.5);          //Louisiana S
		fillquadcoord(53,27.5,-95.0,29.0,-88.5);            //Louisiana OS
		fillquadcoord(54,43.5,-70.5,47.75,-66);        		//Maine E
		fillquadcoord(55,42.75,-71.5,47,-69);             	//Maine W
		fillquadcoord(56,37.75,-79.75,40,-74.75);         	//Maryland
		fillquadcoord(57,41.25,-73.75,43.5,-69);          	//Massachussetts M
		fillquadcoord(58,41,-71.25,41.75,-69);           	//Massachussetts O
		fillquadcoord(59,44.75,-90.75,48.75,-83);         	//Michigan N
		fillquadcoord(60,43.75,-87.5,46.25,-81.75);        	//Michigan C
		fillquadcoord(61,41.25,-87.5,44.5,-81.75);     		//Michigan S
		fillquadcoord(62,46.6,-97.75,49.75,-89);          	//Minnesota N
		fillquadcoord(63,45,-97.5,47.75,-92);           	//Minnesota C
		fillquadcoord(64,43.25,-97.5,46,-91);           	//Minnesota S
		fillquadcoord(65,29.5,-90.25,35.25,-87.7);     		//Mississippi E
		fillquadcoord(66,31.75,-92.0,35.25,-88.75);         //Mississippi W
		fillquadcoord(67,35.75,-92,40.75,-88.75);         	//Missouri E
		fillquadcoord(68,36.25,-94.25,40.75,-90.75);      	//Missouri C
		fillquadcoord(69,36.25,-96.25,40.75,-93);         	//Missouri W
		fillquadcoord(70,44.25,-116.5,49.5,-103.75);      	//Montanna
		fillquadcoord(71,39.75,-104.25,43.25,-94.75);     	//Nebraska
		fillquadcoord(72,34.75,-117.5,42.25,-113.75);     	//Nevada E
		fillquadcoord(73,35.75,-118.75,41.5,-114.75);     	//Nevada C
		fillquadcoord(74,36.75,-120.25,41.5,-116.75);     	//Nevada W
		fillquadcoord(75,42.5,-73.75,42.25,-71.25);         //New Hampshire
		fillquadcoord(76,38.5,-76,41.75,-73.5);             //New Jersey
		fillquadcoord(77,31.75,-106.0,37.25,-102.75);     	//New Mexico E
		fillquadcoord(78,31.50,-108.0,37.25,-104.75);     	//New Mexico C
		fillquadcoord(79,31.0,-109.25,37.25,-106.75);     	//New Mexico W
		fillquadcoord(80,40.5,-76.25,45.25,-73);          	//New York E
		fillquadcoord(81,41.75,-78,44.75,-74.5);          	//New York C
		fillquadcoord(82,41.75,-80,44,-77);                 //New York W
		fillquadcoord(83,40,-74.5,41.5,-71.5);              //New York LI
		fillquadcoord(84,33.25,-84.75,36.75,-75);         	//North Carolina
		fillquadcoord(85,46.75,-104.25,49.25,-96.5);      	//North Dakota N
		fillquadcoord(86,45.75,-104.25,48,-96.25);          //North Dakota S
		fillquadcoord(87,39.75,-85.25,42.75,-80.25);      	//Ohio N
		fillquadcoord(88,38.25,-85.25,40.75,-80.25);      	//Ohio S
		fillquadcoord(89,35.25,-103.25,37.25,-94);          //Oklahoma N
		fillquadcoord(90,33.5,-100.25,35.75,-94);         	//Oklahoma S
		fillquadcoord(91,43.5,-125,46.5,-116.5);          	//Oregon N
		fillquadcoord(92,41.75,-125,45,-116.5);             //Oregon S
		fillquadcoord(93,40.5,-80.75,42.75,-74.5);        	//Pennnsylvania N
		fillquadcoord(94,39.5,-80.75,41.5,-74.5);         	//Pennssylvania S
		fillquadcoord(95,40.75,-72.25,42.25,-70.75);      	//Rhode Island
		fillquadcoord(96,31.5,-83.75,35.5,-78);           	//South Carolina
		fillquadcoord(97,43.75,-104.25,46.25,-96.25);     	//South Dakota N
		fillquadcoord(98,42.75,-104.25,45,-96.25);        	//South Dakota S
		fillquadcoord(99,34.75,-90.25,36.75,-81.6);       	//Tennesee
		fillquadcoord(100,34,-103.25,36.75,-99.75);       	//Texas N
		fillquadcoord(101,31.75,-103.5,34.5,-93.75);      	//Texas NC
		fillquadcoord(102,29.5,-107,32.5,-93.25);         	//Texas C
		fillquadcoord(103,27.75,-105.25,31,-93.5);        	//Texas SC
		fillquadcoord(104,25.5,-100.5,28.5,-96.75);       	//Texas S
		fillquadcoord(105,40.25,-114.5,42.25,-108.75);    	//Utah N
		fillquadcoord(106,38.25,-114.5,41.25,-108.75);    	//Utah C
		fillquadcoord(107,36.75,-114.5,38.75,-108.75);    	//Utah S
		fillquadcoord(108,2.5,-73.75,45.25,-71.25);       	//Vermont
		fillquadcoord(109,37.5,-80.5,39.75,-76.25);       	//Virginia N
		fillquadcoord(110,35.25,-84,38.75,-75);           	//Virginia S
		fillquadcoord(111,46.75,-125,49.75,-116.75);      	//Washington N
		fillquadcoord(112,45.25,-125,47.75,-116.75);      	//Washington S
		fillquadcoord(113,38.5,-82,41,-77.5);             	//West Virginia N
		fillquadcoord(114,37,-82.75,39.5,-78.75);         	//West Virginia S
		fillquadcoord(115,46.5,-93.25,47.5,-87.75);       	//Wisconsin N
		fillquadcoord(116,43.75,-93.25,46,-86);           	//Wisconsin C
		fillquadcoord(117,42.25,-91.75,44.5,-86.75);      	//Wisconsin S
		fillquadcoord(118,40.75,-106.5,45.25,-103.75);    	//Wyoming E
		fillquadcoord(119,40.75,-108.75,45.25,-105.75);   	//Wyoming EC
		fillquadcoord(120,40.75,-110.25,45.25,-107.25);   	//Wyoming WC
		fillquadcoord(121,40.75,-112.25,45.25,-108.75);   	//Wyoming W

		for(int j= 0; j<122;j++){
			if((lat > quadcoord[j][0])&&(lat < quadcoord[j][2])){  
				if((lng > quadcoord[j][1])&&(lng < quadcoord[j][3])){
					return j;
				}
			}
		}
		return 0;

	}

}
