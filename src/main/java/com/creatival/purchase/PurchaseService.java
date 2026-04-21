package com.creatival.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.creatival.SecurityConfig;
import com.creatival.content.Content;
import com.creatival.content.Episode;
import com.creatival.like.TargetType;
import com.creatival.purchase.repository.PurchaseRepository;
import com.creatival.team.TeamService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final TeamService teamService;


    public boolean hasAccess(Users user, Content content) {
        if (content == null) {
            return false;
        }

        if (!content.isPaid()) {
            return true;
        }

        if (user == null) {
            return false;
        }

        if (content.getUser() != null && content.getUser().getId().equals(user.getId())) {
            return true;
        }

        return purchaseRepository.existsByUserAndTargetTypeAndTargetId(user, TargetType.CONTENT, content.getId());
    }

    public boolean hasEpisodeAccess(Users user, Episode episode) {
        if (episode == null) {
            return false;
        }

        if (!episode.isPaid()) {
            return true;
        }

        if (user == null) {
            return false;
        }

        Content content = episode.getSeries().getContent();

        if (content.getUser() != null && content.getUser().getId().equals(user.getId())) {
            return true;
        }

        return purchaseRepository.existsByUserAndTargetTypeAndTargetId(user, TargetType.EPISODE, episode.getId());
    }
    
    public boolean hasAccessForCode(Users user,TargetType targetType, Long targetId) {
    	return purchaseRepository.existsByUserAndTargetTypeAndTargetId(user, targetType, targetId);
    }



    public Purchase createContentPurchase(Users user, Content content) {
        if (user == null) {
            throw new IllegalArgumentException("구매자는 필수입니다.");
        }

        if (content == null) {
            throw new IllegalArgumentException("콘텐츠가 없습니다.");
        }

        if (!content.isPaid()) {
            throw new IllegalStateException("무료 콘텐츠는 구매할 수 없습니다.");
        }

        if (content.getUser() != null && content.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인 콘텐츠는 구매할 수 없습니다.");
        }

        return purchaseRepository.findByUserAndTargetTypeAndTargetId(user, TargetType.CONTENT, content.getId())
                .orElseGet(() -> purchaseRepository.save(
                        Purchase.builder()
                                .user(user)
                                .targetType(TargetType.CONTENT)
                                .targetId(content.getId())
                                .price(content.getPrice())
                                .purchasedAt(LocalDateTime.now())
                                .build()
                ));
    }

    public Purchase createEpisodePurchase(Users user, Episode episode) {
        if (user == null) {
            throw new IllegalArgumentException("구매자는 필수입니다.");
        }

        if (episode == null) {
            throw new IllegalArgumentException("에피소드가 없습니다.");
        }

        if (!episode.isPaid()) {
            throw new IllegalStateException("무료 에피소드는 구매할 수 없습니다.");
        }

        Content content = episode.getSeries().getContent();

        if (content.getUser() != null && content.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인 에피소드는 구매할 수 없습니다.");
        }

        return purchaseRepository.findByUserAndTargetTypeAndTargetId(user, TargetType.EPISODE, episode.getId())
                .orElseGet(() -> purchaseRepository.save(
                        Purchase.builder()
                                .user(user)
                                .targetType(TargetType.EPISODE)
                                .targetId(episode.getId())
                                .price(episode.getPrice())
                                .purchasedAt(LocalDateTime.now())
                                .build()
                ));
    }
    @Transactional(readOnly = true)
    public List<Purchase> getMyPurchases(Users user) {
        return purchaseRepository.findByUserOrderByPurchasedAtDesc(user);
    }

	public void createPurchase(Users user, TargetType targetType, Long targetId, BigDecimal zero, LocalDateTime now) {
		purchaseRepository.save(
	            Purchase.builder()
	                    .user(user)
	                    .targetType(targetType)
	                    .targetId(targetId)
	                    .price(BigDecimal.ZERO)
	                    .purchasedAt(LocalDateTime.now())
	                    .build()
	    );
	}

    
}