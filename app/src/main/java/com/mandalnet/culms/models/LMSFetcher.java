public static void getSubjectResources(Context context, String subjectUrl, ResourceCallback callback) {
    new Thread(() -> {
        try {
            String cookie = LMSPrefs.getCookie(context);
            Document doc = Jsoup.connect(subjectUrl) // Specific subject page
                    .header("Cookie", cookie)
                    .get();

            List<LMSResource> resources = new ArrayList<>();

            // CU LMS mein aksar resources table ya list mein hote hain
            // Humein un links ko 'href' se nikalna hoga
            Elements links = doc.select("a[href*=.pdf], a[href*=.pptx]"); 

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href");
                String type = url.endsWith(".pdf") ? "PDF" : "PPT";
                
                resources.add(new LMSResource(title, url, type));
            }

            callback.onSuccess(resources);
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }).start();
}

public interface ResourceCallback {
    void onSuccess(List<LMSResource> resources);
    void onError(String error);
}