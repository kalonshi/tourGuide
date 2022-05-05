package tourGuide;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;


@Configuration
public class TourGuideModule {
	
	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	/*
	 * @Bean public RewardsService getRewardsService() { return new
	 * RewardsService(); }
	 */
	
	@Bean
	public RewardCentral getRewardCentral() {
		
		return new RewardCentral();
	}
	
	
	/*
	 * @Bean public GpsUtil getGpsUtil() { return new GpsUtil(); }
	 */
	/*
	 * @Bean public RewardCentral getRewardCentral() { return new RewardCentral(); }
	 * 
	 * @Bean public GpsUtilService getGpsUtilService() { return new
	 * GpsUtilService(); }
	 * 
	 * @Bean public RewardsService getRewardsService() { return new
	 * RewardsService(getGpsUtilService(), getRewardCentral()); }
	 */
	
	
	 
}
