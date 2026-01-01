package alivium.controller;

import alivium.domain.entity.User;
import alivium.model.dto.request.FeedbackRequest;
import alivium.model.dto.response.FeedbackAdminResponse;
import alivium.model.dto.response.FeedbackResponse;
import alivium.model.enums.FeedbackStatus;
import alivium.model.enums.FeedbackType;
import alivium.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Validated
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(
            @AuthenticationPrincipal User user
            ,@RequestBody @Valid FeedbackRequest request){
       return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.createFeedback(user.getId(), request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FeedbackResponse>> getMyFeedbacks(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(feedbackService.getMyFeedbacks(user.getId()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Page<FeedbackAdminResponse>> getAllFeedbacks(Pageable pageable){
        return ResponseEntity.ok(feedbackService.getAllFeedbacks(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<FeedbackAdminResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<FeedbackAdminResponse>> findByStatus(@RequestParam FeedbackStatus status){
        return ResponseEntity.ok(feedbackService.findFeedbackByStatus(status));
    }


    @GetMapping("/type")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<List<FeedbackAdminResponse>> findByType(@RequestParam FeedbackType type){
        return ResponseEntity.ok(feedbackService.findFeedbackByType(type));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,@RequestParam FeedbackStatus status){
        feedbackService.updateFeedbackStatus(id,status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Long> count(){
        return ResponseEntity.ok(feedbackService.getTotalFeedbackCount());
    }

    @GetMapping("/avg-rating")
    @PreAuthorize("hasAuthority('ADMIN_ROLE') or hasAuthority('SUPER_ADMIN_ROLE')")
    public ResponseEntity<Double> avgRating(){
        return ResponseEntity.ok(feedbackService.getAverageRating());
    }
}
