package com.example.librewards.repositories;

import static com.example.librewards.resources.RewardCodesTest.rewardCodesAndPoints;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.RewardsRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RewardsRepositoryFake extends RewardsRepository {
    Map<String, Integer> rewardsMap = new HashMap<>();

    @Inject
    public RewardsRepositoryFake(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public String getCode(String code) {
        if (rewardsMap.get(code) == null) {
            return "";
        }
        return code;
    }

    @Override
    public int getCost(String code) {
        return rewardsMap.get(code);
    }

    @Override
    public void populate() {
        rewardsMap = rewardCodesAndPoints;
    }

}
