package com.creatival.redeemcode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.content.Episode;
import com.creatival.like.TargetType;
import com.creatival.purchase.Purchase;
import com.creatival.purchase.repository.PurchaseRepository;
import com.creatival.redeemcode.dto.ResponseRedeemCodeDTO;
import com.creatival.redeemcode.repository.RedeemCodeRepository;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RedeemCodeService {

    private final RedeemCodeRepository redeemCodeRepository;
    private final PurchaseRepository purchaseRepository;
    private final ContentService contentService;

    public RedeemCode createCode(TargetType targetType, Long targetId, Users issuer, LocalDateTime expiredAt) {
        validateIssuerAuthority(targetType, targetId, issuer);

        String code = generateUniqueCode();

        RedeemCode redeemCode = RedeemCode.builder()
                .code(code)
                .targetType(targetType)
                .targetId(targetId)
                .issuer(issuer)
                .used(false)
                .expiredAt(expiredAt)
                .createdAt(LocalDateTime.now())
                .build();

        return redeemCodeRepository.save(redeemCode);
    }

    @Transactional(readOnly = true)
    public List<ResponseRedeemCodeDTO> getIssuedCodes(Users issuer) {
        return redeemCodeRepository.findByIssuerOrderByCreatedAtDesc(issuer)
                .stream()
                .map(ResponseRedeemCodeDTO::from)
                .collect(Collectors.toList());
    }

    public void redeemForTarget(String rawCode, TargetType targetType, Long targetId, Users user) {
        RedeemCode redeemCode = redeemCodeRepository.findByCode(rawCode)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리딤 코드입니다."));

        if (redeemCode.isUsed()) {
            throw new IllegalStateException("이미 사용된 리딤 코드입니다.");
        }

        if (redeemCode.getExpiredAt() != null && redeemCode.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 리딤 코드입니다.");
        }

        if (redeemCode.getTargetType() != targetType || !redeemCode.getTargetId().equals(targetId)) {
            throw new IllegalStateException("이 대상에는 사용할 수 없는 코드입니다.");
        }

        boolean alreadyOwned = purchaseRepository.existsByUserAndTargetTypeAndTargetId(user, targetType, targetId);
        if (alreadyOwned) {
            throw new IllegalStateException("이미 이용 권한이 있습니다.");
        }

        purchaseRepository.save(
                Purchase.builder()
                        .user(user)
                        .targetType(targetType)
                        .targetId(targetId)
                        .price(BigDecimal.ZERO)
                        .purchasedAt(LocalDateTime.now())
                        .build()
        );

        redeemCode.setUsed(true);
        redeemCode.setUsedBy(user);
        redeemCode.setUsedAt(LocalDateTime.now());
    }

    private void validateIssuerAuthority(TargetType targetType, Long targetId, Users issuer) {
        if (targetType == TargetType.CONTENT) {
            Content content = contentService.getContent(targetId);
            if (content.getUser() == null || !content.getUser().getId().equals(issuer.getId())) {
                throw new IllegalStateException("본인 콘텐츠에 대해서만 리딤 코드를 발급할 수 있습니다.");
            }
            return;
        }

        if (targetType == TargetType.EPISODE) {
            Episode episode = contentService.getEpisodeById(targetId);
            Content content = episode.getSeries().getContent();
            if (content.getUser() == null || !content.getUser().getId().equals(issuer.getId())) {
                throw new IllegalStateException("본인 에피소드에 대해서만 리딤 코드를 발급할 수 있습니다.");
            }
            return;
        }

        throw new IllegalStateException("지원하지 않는 리딤 코드 대상입니다.");
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 16)
                    .toUpperCase();

            code = code.substring(0, 4) + "-" +
                   code.substring(4, 8) + "-" +
                   code.substring(8, 12) + "-" +
                   code.substring(12, 16);
        } while (redeemCodeRepository.existsByCode(code));

        return code;
    }
}