# CU-LMS Mobile App Completion TODO

## Plan Breakdown Steps (Approved)

## Phase 1: Fixes & Core Flow
- [x] Step 1: Delete duplicate models/LMSFetcher.java
- [x] Step 2: Update DB/Models (add SemesterEntity/Dao, ResourceEntity/Dao; update Subject with semesterId)

- [ ] Step 3: Update api/LMSFetcher.java (fix selectors/generic scrape, add semester/resources fetch)
- [ ] Step 4: Fix LoginActivity.java (launch MainActivity on success)
- [ ] Step 5: Update MainActivity.java & SubjectAdapter.java (wire clicks)

### Phase 2: Resource & Viewers
- [ ] Step 6: Create ResourceAdapter.java & activity_resource.xml; Update ResourceActivity.java (fetch/display categorized resources)
- [ ] Step 7: Fix PdfViewerActivity.java (error handling, PPT support)
- [ ] Step 8: Update DataRepository.java (resources support)

### Phase 3: Features & Polish
- [ ] Step 9: Add ProfileActivity + layout (personalized learning profile)
- [ ] Step 10: Update layouts/UI (semester view if added, profile btn)
- [ ] Step 11: Build/test (`./gradlew build`, run app)
- [ ] Step 12: Final enhancements (fake data if scrape fails, pull-refresh)

Current Progress: Starting Step 1
