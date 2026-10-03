[33mcommit 26ccc9eb912a8b320d8d59d6e5480248f6271f81[m[33m ([m[1;36mHEAD[m[33m -> [m[1;32mmain[m[33m)[m
Author: V1KTH0RX <184307138+V1KTH0RX@users.noreply.github.com>
Date:   Sat Oct 3 14:49:13 2026 -0600

    feat(ui): tema Material 3 Expressive y navegación inferior

[1mdiff --git a/app/build.gradle.kts b/app/build.gradle.kts[m
[1mindex 1044bec..39ccdde 100644[m
[1m--- a/app/build.gradle.kts[m
[1m+++ b/app/build.gradle.kts[m
[36m@@ -40,6 +40,7 @@[m [mdependencies {[m
     implementation(platform(libs.androidx.compose.bom))[m
     implementation(libs.androidx.activity.compose)[m
     implementation(libs.androidx.compose.material3)[m
[32m+[m[32m    implementation(libs.androidx.compose.material.icons.extended)[m
     implementation(libs.androidx.compose.ui)[m
     implementation(libs.androidx.compose.ui.graphics)[m
     implementation(libs.androidx.compose.ui.tooling.preview)[m
[1mdiff --git a/gradle/libs.versions.toml b/gradle/libs.versions.toml[m
[1mindex e582579..0977683 100644[m
[1m--- a/gradle/libs.versions.toml[m
[1m+++ b/gradle/libs.versions.toml[m
[36m@@ -29,6 +29,7 @@[m [mandroidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-toolin[m
 androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }[m
 androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }[m
 androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }[m
[32m+[m[32mandroidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }[m
 androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }[m
 androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }[m
 androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewModelCompose" }[m
