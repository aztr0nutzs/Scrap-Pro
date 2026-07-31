# Project Reconstruction Report

## Method

This inventory was captured from Git `HEAD` before reconstruction. Every tracked file was read, sized, SHA-256 hashed, and classified using its contents, Kotlin package/import declarations, Android conventions, Gradle references, test runners, or web references. Text sources are preserved at their final paths. Binary payloads are intentionally excluded from this PR, while their authoritative hashes and exact restore destinations remain recorded below.

## Complete original-to-final inventory

| Original path | Identified file type | Final path | Action | Reason | Duplicate status | Original bytes | SHA-256 | Package | Import/reference sample |
|---|---|---|---|---|---|---:|---|---|---|
| .env.example | Project root/support file | .env.example | Retained at root | Gradle/web/project convention | Unique | 83 | c358472d0e2299bd69031486684073bb38fecfad85fd38ab9587a40823f37361 | — | — |
| .gitignore | Project root/support file | .gitignore | Retained at root | Gradle/web/project convention | Unique | 7 | 5b46eb48d96e8571e8c93e4ef4f6ffbe6807b6d7350664a36a064e1167a32718 | — | — |
| AndroidManifest.xml | Android XML resource or manifest | app/src/main/AndroidManifest.xml | Moved | Android resource convention | Unique | 1243 | 23228b5f5272044a994851a4dc590a43bc4671283933ecdd9c76cdda08061401 | — | — |
| AppNavigation.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/navigation/AppNavigation.kt | Moved | Package declaration/source-set convention | Unique | 3846 | c260e84ca47eb2164fc30f10da52e34764e77b7a49506c417ebd8230d758fcff | com.example.ui.navigation | androidx.compose.foundation.layout.padding, androidx.compose.material3.Icon, androidx.compose.material3.MaterialTheme … |
| CalculationEnginesTest.kt | Kotlin test source | app/src/test/java/com/example/CalculationEnginesTest.kt | Moved | Package declaration/source-set convention | Unique | 2389 | b7e1d8e7698db88b8724d1e8df7f1d43a4dcda402d1bba653528c09e43834ebf | com.example | com.example.domain.engine.PayloadSafetyEngine, com.example.domain.engine.ProfitCalculationEngine, com.example.domain.engine.WireStrippingRoiEngine … |
| CalculatorsTabScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/calculators/CalculatorsTabScreen.kt | Moved | Package declaration/source-set convention | Unique | 1503 | 82e950599449d909d9c98e550aad8441ec9e7ed309324c22a0dbe19bda3f9394 | com.example.ui.calculators | com.example.R, androidx.compose.foundation.layout.Box, androidx.compose.foundation.layout.Column … |
| Color.kt | Kotlin Android source | app/src/main/java/com/example/ui/theme/Color.kt | Moved | Package declaration/source-set convention | Unique | 532 | 18f6eb0c72444b675826c2e4b1b511e510dd5bbd83a760e80edac82af92f8aba | com.example.ui.theme | androidx.compose.ui.graphics.Color |
| ContractorOutreachScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/outreach/ContractorOutreachScreen.kt | Moved | Package declaration/source-set convention | Unique | 10204 | 002a64da74de93f0ef7e8dbe6197b17fe68c1d69c3d6ff999f920f4353707f62 | com.example.ui.outreach | android.content.Context, android.content.Intent, android.net.Uri … |
| Converters.kt | Kotlin Android source | app/src/main/java/com/example/data/local/Converters.kt | Moved | Package declaration/source-set convention | Unique | 315 | a41c62446ab092b7eb21da59a6cb03f3f7f0b62e826bfb6cec06f02c49e831d5 | com.example.data.local | androidx.room.TypeConverter, com.example.domain.model.MetalGrade |
| ExampleInstrumentedTest.kt | Kotlin test source | app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt | Moved | Package declaration/source-set convention | Unique | 630 | af79227e88d1ea0ae10279e9dd2a16f0e290766cd9d90620f4339449cb28067e | com.example | androidx.test.ext.junit.runners.AndroidJUnit4, androidx.test.platform.app.InstrumentationRegistry, org.junit.Assert.* … |
| ExampleRobolectricTest.kt | Kotlin test source | app/src/test/java/com/example/ExampleRobolectricTest.kt | Moved | Package declaration/source-set convention | Unique | 586 | 9eb0ffd4c37f397f37a28a88b2bf6e9bd62158ab713f0c94f6e58f1a317a883e | com.example | android.content.Context, androidx.test.core.app.ApplicationProvider, org.junit.Assert.assertEquals … |
| ExampleUnitTest.kt | Kotlin test source | app/src/test/java/com/example/ExampleUnitTest.kt | Moved | Package declaration/source-set convention | Unique | 325 | 23f42e47e120caa28734bae3302e7277bbd805e597302f7cb79354641d441cdb | com.example | org.junit.Assert.*, org.junit.Test |
| FORENSIC_AUDIT.md | Project documentation | FORENSIC_AUDIT.md | Retained at root | Gradle/web/project convention | Unique | 5999 | c284d291e1535e8acb1e8b22852577c25babf1c32656c909a3310418e3254892 | — | — |
| GreetingScreenshotTest.kt | Kotlin test source | app/src/test/java/com/example/GreetingScreenshotTest.kt | Moved | Package declaration/source-set convention | Unique | 993 | 11284c93ed035d1a10e25715563ab1b906c0b751595788362e5b521681912ce8 | com.example | androidx.compose.ui.test.junit4.createComposeRule, androidx.compose.ui.test.onRoot, com.example.ui.theme.MyApplicationTheme … |
| HeaderBanner.kt | Kotlin Android source | app/src/main/java/com/example/ui/core/HeaderBanner.kt | Moved | Package declaration/source-set convention | Unique | 487 | cffe5891a15cc86c2cd2656904bb83746024e04c37f506d68790212880e767b7 | com.example.ui.core | androidx.annotation.DrawableRes, androidx.compose.runtime.Composable, androidx.compose.ui.Modifier |
| HomeScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/home/HomeScreen.kt | Moved | Package declaration/source-set convention | Unique | 6860 | 2976d42ae915f008c1f0f9677e7e51db8dc15b9620bcee19de934816a1707eb4 | com.example.ui.home | androidx.compose.foundation.layout.Arrangement, androidx.compose.foundation.layout.Column, androidx.compose.foundation.layout.PaddingValues … |
| KnowledgeBaseData.kt | Kotlin Android source | app/src/main/java/com/example/domain/model/KnowledgeBaseData.kt | Moved | Package declaration/source-set convention | Unique | 4835 | 99d5e31fccdffdb45500aa1e172a103667fbe0fbb449d450ae0d391793655de6 | com.example.domain.model | — |
| KnowledgeBaseScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/knowledge/KnowledgeBaseScreen.kt | Moved | Package declaration/source-set convention | Unique | 6014 | 3f1d137038d8f441aef0f280a5c8bec4c80a7a2cea80bc68e6db4d4090ca676d | com.example.ui.knowledge | androidx.compose.animation.AnimatedVisibility, androidx.compose.animation.core.tween, androidx.compose.animation.expandVertically … |
| LICENSE | Project root/support file | LICENSE | Retained at root | Gradle/web/project convention | Unique | 11357 | c71d239df91726fc519c6eb72d318ec65820627232b2f796219e87dcf35d0ab4 | — | — |
| LocationRepository.kt | Data repository source | app/src/main/java/com/example/data/repository/LocationRepository.kt | Moved | Package declaration/source-set convention | Unique | 4661 | fe465f4d67054d9222595e50105d441ca844d098a77de3eed5553a11b7e63985 | com.example.data.repository | android.annotation.SuppressLint, android.location.Location, com.example.data.domain.ScrapYardLocation … |
| MainActivity.kt | Kotlin Android source | app/src/main/java/com/example/MainActivity.kt | Moved | Package declaration/source-set convention | Unique | 512 | b6db4b1e18eebd61889dfa770869963b13023379afdbc34ff56b71e27c9a1b77 | com.example | android.os.Bundle, androidx.activity.ComponentActivity, androidx.activity.compose.setContent … |
| MetalAssetRegistry.kt | Kotlin Android source | app/src/main/java/com/example/ui/core/MetalAssetRegistry.kt | Moved | Package declaration/source-set convention | Unique | 2418 | 88774b87547bfb2b016d20683dd7e22aac3d73a9064f61583fe91a931ea25e02 | com.example.ui.core | androidx.annotation.DrawableRes, androidx.compose.ui.graphics.Color, com.example.R … |
| MetalIdWizard.kt | Kotlin Android source | app/src/main/java/com/example/ui/wizard/MetalIdWizard.kt | Moved | Package declaration/source-set convention | Unique | 12292 | b051c9f5bfd2865212cb2037e025f87f394caa5d1260e020573a6a7fa34f2c43 | com.example.ui.wizard | com.example.R, androidx.compose.foundation.layout.*, androidx.compose.foundation.rememberScrollState … |
| MetalModels.kt | Kotlin Android source | app/src/main/java/com/example/domain/model/MetalModels.kt | Moved | Package declaration/source-set convention | Unique | 1323 | 39a171903287b51a00692b1e855d61bcaa415d9caac71fce59e5c431f363f6f0 | com.example.domain.model | — |
| MetalUiComponents.kt | Kotlin Android source | app/src/main/java/com/example/ui/core/MetalUiComponents.kt | Moved | Package declaration/source-set convention | Unique | 7348 | 51e8a5825e730c49857582eb9fa23f129212684e27aa7ffd604eaaa0b4b943ba | com.example.ui.core | androidx.compose.foundation.Image, androidx.compose.foundation.background, androidx.compose.foundation.border … |
| PayloadSafety.kt | Kotlin Android source | app/src/main/java/com/example/ui/calculators/PayloadSafety.kt | Moved | Package declaration/source-set convention | Unique | 15724 | 5f51996d84e7dbeb2eb06e2c3ca127e160be4a4cb12c78f9ac7644f4c2f77a95 | com.example.ui.calculators | com.example.R, androidx.compose.foundation.Canvas, androidx.compose.foundation.background … |
| PayloadSafetyEngine.kt | Calculation/domain engine source | app/src/main/java/com/example/domain/engine/PayloadSafetyEngine.kt | Moved | Package declaration/source-set convention | Unique | 3465 | f3e60f8bac8bc4069ff2ad906db0134f80c222110a4d02c40e2362501e9cc3eb | com.example.domain.engine | com.example.domain.model.CargoLoad, com.example.domain.model.VehicleRating |
| ProfitCalculationEngine.kt | Calculation/domain engine source | app/src/main/java/com/example/domain/engine/ProfitCalculationEngine.kt | Moved | Package declaration/source-set convention | Unique | 2790 | 884b5d6d71dce8bb3ed425baee8663d8fe20bca5f30f93a89a55e254dc71d02b | com.example.domain.engine | com.example.domain.model.LoadItem, com.example.domain.model.MetalGrade |
| ProfitCalculator.kt | Kotlin Android source | app/src/main/java/com/example/ui/calculators/ProfitCalculator.kt | Moved | Package declaration/source-set convention | Unique | 17523 | dc604c0efe243c16b39fc4fad786f3f0ce6836601b417966741fe8519cb474f7 | com.example.ui.calculators | com.example.R, androidx.compose.foundation.Canvas, androidx.compose.foundation.background … |
| README.md | Project documentation | README.md | Retained at root | Gradle/web/project convention | Unique | 1180 | 8fdff8b6ac17418b006e04cba16c5bbb981de2221292523853b49d2f30832578 | — | — |
| ScrapProDatabase.kt | Kotlin Android source | app/src/main/java/com/example/data/local/ScrapProDatabase.kt | Moved | Package declaration/source-set convention | Unique | 1264 | d0630d5b225a9b36ce987a929ce191c0dc8e451a19823345f72fbeb2a09c1e97 | com.example.data.local | android.content.Context, androidx.room.Database, androidx.room.Room … |
| ScrapYardLocation.kt | Kotlin Android source | app/src/main/java/com/example/data/domain/ScrapYardLocation.kt | Moved | Package declaration/source-set convention | Unique | 1347 | 06bbb0b3ad5a75af2093ee2ff196d0f061040ebcd23a578fc54f5ca0f7b179f2 | com.example.data.domain | java.time.DayOfWeek, java.time.LocalTime, java.time.ZoneId … |
| Screen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/navigation/Screen.kt | Moved | Package declaration/source-set convention | Unique | 1101 | 550459bb46e35ddfaf4fd3f2b31de795c46acd63ef5d167bcc595c2664fe572f | com.example.ui.navigation | androidx.compose.material.icons.Icons, androidx.compose.material.icons.filled.Calculate, androidx.compose.material.icons.filled.Home … |
| ScreenHeaderBanner.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/core/ScreenHeaderBanner.kt | Moved | Package declaration/source-set convention | Unique | 794 | cc27b2835a36cd6149e4b98c33e2647d8bb56a1a0613dad5cedcab444695f72b | com.example.ui.core | androidx.annotation.DrawableRes, androidx.compose.foundation.Image, androidx.compose.foundation.layout.fillMaxWidth … |
| Theme.kt | Kotlin Android source | app/src/main/java/com/example/ui/theme/Theme.kt | Moved | Package declaration/source-set convention | Unique | 945 | 199eb24ca83675147b352a137fe0a21879d8b63b815aa66ecced5d84525ec233 | com.example.ui.theme | androidx.compose.material3.MaterialTheme, androidx.compose.material3.darkColorScheme, androidx.compose.runtime.Composable … |
| ToolsTabScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/wizard/ToolsTabScreen.kt | Moved | Package declaration/source-set convention | Unique | 1386 | d4174532611bc0a42f0845d557019687b38d399f0d2b4f111b84263af21190ec | com.example.ui.wizard | androidx.compose.foundation.layout.Column, androidx.compose.foundation.layout.fillMaxSize, androidx.compose.material3.* … |
| TripLogDao.kt | Room DAO source | app/src/main/java/com/example/data/local/dao/TripLogDao.kt | Moved | Package declaration/source-set convention | Unique | 675 | 72a63b6d3a891939ad41da0369fc873f593e6abb7159bc3a6e8659f665d20fda | com.example.data.local.dao | androidx.room.*, com.example.data.local.entity.TripLogEntity, kotlinx.coroutines.flow.Flow |
| TripLogEntity.kt | Room entity source | app/src/main/java/com/example/data/local/entity/TripLogEntity.kt | Moved | Package declaration/source-set convention | Unique | 747 | ffdaf2ec0c57a6bdcb379830f9447bfd8ddfee4526ffd22d9484097e5e860419 | com.example.data.local.entity | androidx.room.Entity, androidx.room.ForeignKey, androidx.room.Index … |
| TripLogRepository.kt | Data repository source | app/src/main/java/com/example/data/repository/TripLogRepository.kt | Moved | Package declaration/source-set convention | Unique | 787 | 2f80b40c66a4846ebcb1fe3a1bfe4c6c4222f8bc3dbc3ed575fa9dd64215a482 | com.example.data.repository | com.example.data.local.dao.TripLogDao, com.example.data.local.entity.TripLogEntity, kotlinx.coroutines.flow.Flow |
| Type.kt | Kotlin Android source | app/src/main/java/com/example/ui/theme/Type.kt | Moved | Package declaration/source-set convention | Unique | 3197 | c5e7f07da33a2c957274a2d310b691ae3b907acc6bcd8af8852299e1e714f4c2 | com.example.ui.theme | androidx.compose.material3.Typography, androidx.compose.ui.text.TextStyle, androidx.compose.ui.text.font.FontFamily … |
| UiState.kt | Kotlin Android source | app/src/main/java/com/example/ui/core/UiState.kt | Moved | Package declaration/source-set convention | Unique | 216 | 2624a6285519cf3609a3e655386066b1c49ec656ec8aa54498842582eb4ed6a1 | com.example.ui.core | — |
| VehicleModels.kt | Kotlin Android source | app/src/main/java/com/example/domain/model/VehicleModels.kt | Moved | Package declaration/source-set convention | Unique | 419 | 4058599dfd3458fce0d1f1e30bd261b24997dae19794bcd53a2976f7a3dbf0f1 | com.example.domain.model | — |
| WireStripping.kt | Kotlin Android source | app/src/main/java/com/example/ui/calculators/WireStripping.kt | Moved | Package declaration/source-set convention | Unique | 14581 | 373ec188141eb718e48b0e4de989832101592d7be7d0172a464529990f193e6a | com.example.ui.calculators | com.example.R, androidx.compose.foundation.layout.*, androidx.compose.foundation.rememberScrollState … |
| WireStrippingRoiEngine.kt | Calculation/domain engine source | app/src/main/java/com/example/domain/engine/WireStrippingRoiEngine.kt | Moved | Package declaration/source-set convention | Unique | 3154 | de6f4a5bf43f1de3aee63a1a0827e26a1585cc2267e671d2207f8de0d4f8a4ec | com.example.domain.engine | com.example.domain.model.WireCategory |
| YardDao.kt | Room DAO source | app/src/main/java/com/example/data/local/dao/YardDao.kt | Moved | Package declaration/source-set convention | Unique | 1104 | d49813798c557e6434b6fbc2b0b835138db5682e59295f3d1e464496dc99941a | com.example.data.local.dao | androidx.room.*, com.example.data.local.entity.YardEntity, com.example.data.local.entity.YardPriceEntity … |
| YardEntity.kt | Room entity source | app/src/main/java/com/example/data/local/entity/YardEntity.kt | Moved | Package declaration/source-set convention | Unique | 336 | 6c56c29ea2211529b3d0e75d4feeacb8511be8db8febaf706233b13da461e80b | com.example.data.local.entity | androidx.room.Entity, androidx.room.PrimaryKey |
| YardFinderScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/yardfinder/YardFinderScreen.kt | Moved | Package declaration/source-set convention | Unique | 18293 | b10b2906aef3ffbf9d218977a9a3933cbe1a6e01b265910e35cee235c99a5588 | com.example.ui.yardfinder | com.example.R, android.content.Context, android.content.Intent … |
| YardFinderViewModel.kt | Kotlin Android source | app/src/main/java/com/example/ui/yardfinder/YardFinderViewModel.kt | Moved | Package declaration/source-set convention | Unique | 4325 | 0defd5c7aeb5aa9eebd0493b003b5f637601448b5ceb4589dd8a9b7e2f533d84 | com.example.ui.yardfinder | android.app.Application, androidx.lifecycle.AndroidViewModel, androidx.lifecycle.viewModelScope … |
| YardLoggerViewModel.kt | Kotlin Android source | app/src/main/java/com/example/ui/logger/YardLoggerViewModel.kt | Moved | Package declaration/source-set convention | Unique | 3456 | e5a335ff93ba834a634d4e8115288ce1ef6ab920ea252fcbaf163143f4269a39 | com.example.ui.logger | android.app.Application, androidx.lifecycle.AndroidViewModel, androidx.lifecycle.viewModelScope … |
| YardPriceEntity.kt | Room entity source | app/src/main/java/com/example/data/local/entity/YardPriceEntity.kt | Moved | Package declaration/source-set convention | Unique | 721 | 050a10309af281b7f033bd2380250b127c6578427273ee2ff6837149769ecfcd | com.example.data.local.entity | androidx.room.Entity, androidx.room.ForeignKey, androidx.room.Index … |
| YardRepository.kt | Data repository source | app/src/main/java/com/example/data/repository/YardRepository.kt | Moved | Package declaration/source-set convention | Unique | 1105 | 1513273bae3473a4e04e4bc891393848acf1ff6fd8b67484552f60b3fdefecf6 | com.example.data.repository | com.example.data.local.dao.YardDao, com.example.data.local.entity.YardEntity, com.example.data.local.entity.YardPriceEntity … |
| YardTrackerScreen.kt | Compose UI/navigation source | app/src/main/java/com/example/ui/logger/YardTrackerScreen.kt | Moved | Package declaration/source-set convention | Unique | 6060 | 7a6bd6f9fe8185dee57f7618bf0036479557be57a3bf9eb9e336279d669ae85b | com.example.ui.logger | androidx.compose.foundation.layout.*, androidx.compose.foundation.lazy.LazyColumn, androidx.compose.foundation.lazy.items … |
| app.js | Web preview behavior | preview/app.js | Moved | Gradle/web/project convention | Unique | 6201 | 8e1aaa16a617267150ed67585bc7e9e45000274d31c18677c9d9cf4d1bf6ea86 | — | — |
| backup_rules.xml | Android XML resource or manifest | app/src/main/res/xml/backup_rules.xml | Moved | Android resource convention | Unique | 479 | 173c90cbeb773e6a65b430581a85720dc5c7ddba17d543cfefe3d2aed9b9a7a4 | — | — |
| build.gradle.kts | Gradle Kotlin build configuration | app/build.gradle.kts | Moved to app module; minimal root build created | Gradle/web/project convention | Unique | 4317 | 7faccf70f731a74a3087d29e2d3dd857479aeec34f3f71d9136f1da12b446ebb | — | — |
| colors.xml | Android XML resource or manifest | app/src/main/res/values/colors.xml | Moved | Android resource convention | Unique | 379 | 57681038afd005dcd260eabdd6d16619709c4a3fe20324362ea00e093e85b5fd | — | — |
| data_extraction_rules.xml | Android XML resource or manifest | app/src/main/res/xml/data_extraction_rules.xml | Moved | Android resource convention | Unique | 552 | 30ae995624fd57d839bf0d638900f8d2c17c703d6e7d6c8640708a8a7cac2402 | — | — |
| gradle-wrapper.jar | Gradle wrapper bootstrap JAR | Excluded from this PR; restore to `gradle/wrapper/gradle-wrapper.jar` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 55616 | 3dc39ad650d40f6c029bd8ff605c6d95865d657dbfdeacdb079db0ddfffedf9f | — | — |
| gradle-wrapper.properties | Gradle/wrapper configuration | gradle/wrapper/gradle-wrapper.properties | Moved | Gradle/web/project convention | Unique | 252 | a180163249c7252412ddaee1c7adcf59f6494b1183136f690fbd9a3b8aaed00e | — | — |
| gradle.properties | Gradle/wrapper configuration | gradle.properties | Retained at root | Gradle/web/project convention | Unique | 1610 | 32fc24a2a7f2aa700a61419d8a9a6db8c9ff82d12968056a8bc0850e80ddc269 | — | — |
| gradlew | Project root/support file | gradlew | Retained at root | Gradle/web/project convention | Unique | 5960 | 261d896f782ec3181add62d77d5b1b9e1d0d599757ec108d857bad1eec2e7615 | — | — |
| gradlew.bat | Windows Gradle wrapper script | gradlew.bat | Retained at root | Gradle/web/project convention | Unique | 2942 | 0dc553fa66b53404638a5cc51a2fbfbc4f00b661fdc00b52da3acb5ed6868342 | — | — |
| greeting.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/test/screenshots/greeting.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2868 | 08b77a026d9f6bc52b9404021e164eaf16d033701ddc0cdb1caabe6f5e7ed01b | — | — |
| ic_launcher.webp | WebP launcher resource | Excluded from this PR; restore to `app/src/main/res/mipmap-mdpi/ic_launcher.webp` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 5743 | fe039e8aff6718652748d5d2daa5743cbf38759392a7feb723e7c032349b6e81 | — | — |
| ic_launcher.xml | Android XML resource or manifest | app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml | Moved | Android resource convention | Identical content: ic_launcher.xml, ic_launcher_round.xml | 344 | 6d0988175971f41bdc83eadb144af3d84c40052fa125e90edbe7d89227be4b25 | — | — |
| ic_launcher_background.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_launcher_background.xml | Moved | Android resource convention | Unique | 5606 | ed423c73a6f40a4d2909f0901e60527b3a807cd59e1b5593bcaae1808b1c6321 | — | — |
| ic_launcher_foreground.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_launcher_foreground.xml | Moved | Android resource convention | Unique | 1703 | b27d486b9529d35fe31bda300ab1c2323407e848e88f7a713e105f6fd2c7d7ef | — | — |
| ic_launcher_round.webp | WebP launcher resource | Excluded from this PR; restore to `app/src/main/res/mipmap-mdpi/ic_launcher_round.webp` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 11709 | 4d95c8cb035a1827eef9c0cf065e4be0f8fc0b86d6c420e9f3bccc366fba921d | — | — |
| ic_launcher_round.xml | Android XML resource or manifest | app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml | Moved | Android resource convention | Identical content: ic_launcher.xml, ic_launcher_round.xml | 344 | 6d0988175971f41bdc83eadb144af3d84c40052fa125e90edbe7d89227be4b25 | — | — |
| ic_metal_aluminum.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_aluminum.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| ic_metal_base.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_base.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| ic_metal_brass.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_brass.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| ic_metal_copper.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_copper.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| ic_metal_iron.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_iron.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| ic_metal_steel.xml | Android XML resource or manifest | app/src/main/res/drawable/ic_metal_steel.xml | Moved | Android resource convention | Identical content: ic_metal_aluminum.xml, ic_metal_base.xml, ic_metal_brass.xml, ic_metal_copper.xml, ic_metal_iron.xml, ic_metal_steel.xml | 342 | 26e7ad128c1ea1c19370073206629d0097065b4a54b757d4b39aed6168cd07d5 | — | — |
| id_wizard.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/id_wizard.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2042446 | b51b6521a0bb1aed6db01839668a3e8375f6adb5ffe066217b700e8d8a19fb01 | — | — |
| index.html | Web preview markup | preview/index.html | Moved | Gradle/web/project convention | Unique | 6418 | d2bc2068b141d78dbd405a7c456cf2abcc471c10c431a7dbf01a8902e65a717e | — | — |
| libs.versions.toml | Gradle version catalog | gradle/libs.versions.toml | Moved | Gradle/web/project convention | Unique | 7028 | 8cbd3928bcf8c6f17a26a615092362b9f28016cecb864e6274befff9483485ae | — | — |
| load_bal.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/load_bal.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2191875 | bc425badd93451e6a97201066e97999f21810928f153257d13e7301191ec4e89 | — | — |
| load_calc.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/load_calc.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2130089 | d215919499a6ec994ca0b0a287835e8f69f2a195bb9441e7d38900eea765f228 | — | — |
| metadata.json | Project/package metadata | metadata.json | Retained at root | Gradle/web/project convention | Unique | 146 | 403f1567b020bfd6676ee60f988944620a10da4700f644d9964fe7483c78b8cf | — | — |
| package-lock.json | Project/package metadata | package-lock.json | Retained at root | Gradle/web/project convention | Unique | 34216 | cdd6879af8ee16ce11cde04809e9537b1693fb28dfe16b90585364343b686a03 | — | — |
| package.json | Project/package metadata | package.json | Retained at root | Gradle/web/project convention | Unique | 324 | 57063069b0cb22c866041b5a004946139850551c18d6e26154d151b55be897ad | — | — |
| price_track.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/price_track.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2185682 | 7b4731088e9039c5e8cc28fdecf824716039ceb6b2a88c45ec857a5c46f0808e | — | — |
| proguard-rules.pro | Project root/support file | app/proguard-rules.pro | Moved | Gradle/web/project convention | Unique | 751 | 1cf8c57e8f79c250b0af9c1a5a4edad71a5c348a79ab70243b6bae086c150ad2 | — | — |
| scrap_calc.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/scrap_calc.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2003092 | 2db9f476668c135685da58c2ef9848c375cbf8a15b997dfadb464b58fe0ac24a | — | — |
| scrap_icon.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/scrap_icon.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2954395 | 3bd9339afc24038bb3009b3c18995330c85a87af1d54b273d32840e508cdac29 | — | — |
| scrap_main.png | PNG artwork/resource | Excluded from this PR; restore to `app/src/main/res/drawable/scrap_main.png` | Mapped; binary payload removed | Binary-free pull-request requirement | Unique | 2171468 | cd5e0954341046372370407fa67df9dc22ebfaf5c88d3eb25882241d2e5fc83f | — | — |
| settings.gradle.kts | Gradle Kotlin build configuration | settings.gradle.kts | Retained at root | Gradle/web/project convention | Unique | 548 | f91418c897d4e92b4fc28e6b33db66b9b60664dca0c04f5ff2a170adb079c4a6 | — | — |
| strings.xml | Android XML resource or manifest | app/src/main/res/values/strings.xml | Moved | Android resource convention | Unique | 71 | ab8b034325ac355992f8891788b996f0b31c4c223af92405f2bb058e8c1e3301 | — | — |
| styles.css | Web preview stylesheet | preview/styles.css | Moved | Gradle/web/project convention | Unique | 3108 | 3c1aa6cfc50f5a15917644bad2ad7086ae30f50245d855100e4cb2becf28430d | — | — |
| themes.xml | Android XML resource or manifest | app/src/main/res/values/themes.xml | Moved | Android resource convention | Unique | 155 | e8288b65767f4f28854d168c7b19c308579d3703bdf3fc26c2b266e19148f465 | — | — |
| verify_project.py | Project structural verifier | scripts/verify_project.py | Moved | Gradle/web/project convention | Unique | 3428 | 50a681ae5ed65a28786c01370096927d3634d4b48a5f1172386647ea04f7fb0d | — | re, struct, sys |

## Derived files and duplicates

| Origin | Final path | Action | Reason |
|---|---|---|---|
| Original flattened `build.gradle.kts` | `build.gradle.kts` | Created minimal root plugin declaration | The original content is an Android application DSL and therefore belongs to `app/build.gradle.kts`; the root file declares shared plugins only. |
| `scrap_icon.png` | `preview/assets/scrap_icon.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `scrap_main.png` | `preview/assets/scrap_main.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `id_wizard.png` | `preview/assets/id_wizard.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `load_calc.png` | `preview/assets/load_calc.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `load_bal.png` | `preview/assets/load_bal.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `price_track.png` | `preview/assets/price_track.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |
| `scrap_calc.png` | `preview/assets/scrap_calc.png` | Mapped; binary payload removed | Restore the original bytes to both Android and preview destinations in the asset-only follow-up. |

## Orphan and removal decisions

- **Removed from this PR:** 18 binary payloads: the wrapper JAR, seven Android PNGs, seven preview PNG copies, two launcher WebPs, and one screenshot baseline. Their original hashes and exact destinations remain documented.
- **Genuine orphans:** none. The omitted `greeting.png` remains mapped as the Roborazzi screenshot baseline at `app/src/test/screenshots/greeting.png`.
- **Generated directories:** pre-existing `node_modules/`, Gradle/build output, `dist-preview/`, `local.properties`, and the final ZIP are ignored and excluded from source control/archive input.
- **Unresolved duplicates:** none. Cross-target artwork hashes were resolved before binary removal; restore the authoritative bytes in an asset-only follow-up.

## Final directory tree

```text
.env.example
.gitignore
BUILD_VERIFICATION.md
FORENSIC_AUDIT.md
LICENSE
PROJECT_RECONSTRUCTION_REPORT.md
README.md
app/build.gradle.kts
app/proguard-rules.pro
app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt
app/src/main/AndroidManifest.xml
app/src/main/java/com/example/MainActivity.kt
app/src/main/java/com/example/data/domain/ScrapYardLocation.kt
app/src/main/java/com/example/data/local/Converters.kt
app/src/main/java/com/example/data/local/ScrapProDatabase.kt
app/src/main/java/com/example/data/local/dao/TripLogDao.kt
app/src/main/java/com/example/data/local/dao/YardDao.kt
app/src/main/java/com/example/data/local/entity/TripLogEntity.kt
app/src/main/java/com/example/data/local/entity/YardEntity.kt
app/src/main/java/com/example/data/local/entity/YardPriceEntity.kt
app/src/main/java/com/example/data/repository/LocationRepository.kt
app/src/main/java/com/example/data/repository/TripLogRepository.kt
app/src/main/java/com/example/data/repository/YardRepository.kt
app/src/main/java/com/example/domain/engine/PayloadSafetyEngine.kt
app/src/main/java/com/example/domain/engine/ProfitCalculationEngine.kt
app/src/main/java/com/example/domain/engine/WireStrippingRoiEngine.kt
app/src/main/java/com/example/domain/model/KnowledgeBaseData.kt
app/src/main/java/com/example/domain/model/MetalModels.kt
app/src/main/java/com/example/domain/model/VehicleModels.kt
app/src/main/java/com/example/ui/calculators/CalculatorsTabScreen.kt
app/src/main/java/com/example/ui/calculators/PayloadSafety.kt
app/src/main/java/com/example/ui/calculators/ProfitCalculator.kt
app/src/main/java/com/example/ui/calculators/WireStripping.kt
app/src/main/java/com/example/ui/core/HeaderBanner.kt
app/src/main/java/com/example/ui/core/MetalAssetRegistry.kt
app/src/main/java/com/example/ui/core/MetalUiComponents.kt
app/src/main/java/com/example/ui/core/ScreenHeaderBanner.kt
app/src/main/java/com/example/ui/core/UiState.kt
app/src/main/java/com/example/ui/home/HomeScreen.kt
app/src/main/java/com/example/ui/knowledge/KnowledgeBaseScreen.kt
app/src/main/java/com/example/ui/logger/YardLoggerViewModel.kt
app/src/main/java/com/example/ui/logger/YardTrackerScreen.kt
app/src/main/java/com/example/ui/navigation/AppNavigation.kt
app/src/main/java/com/example/ui/navigation/Screen.kt
app/src/main/java/com/example/ui/outreach/ContractorOutreachScreen.kt
app/src/main/java/com/example/ui/theme/Color.kt
app/src/main/java/com/example/ui/theme/Theme.kt
app/src/main/java/com/example/ui/theme/Type.kt
app/src/main/java/com/example/ui/wizard/MetalIdWizard.kt
app/src/main/java/com/example/ui/wizard/ToolsTabScreen.kt
app/src/main/java/com/example/ui/yardfinder/YardFinderScreen.kt
app/src/main/java/com/example/ui/yardfinder/YardFinderViewModel.kt
app/src/main/res/drawable/ic_launcher_background.xml
app/src/main/res/drawable/ic_launcher_foreground.xml
app/src/main/res/drawable/ic_metal_aluminum.xml
app/src/main/res/drawable/ic_metal_base.xml
app/src/main/res/drawable/ic_metal_brass.xml
app/src/main/res/drawable/ic_metal_copper.xml
app/src/main/res/drawable/ic_metal_iron.xml
app/src/main/res/drawable/ic_metal_steel.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
app/src/main/res/values/colors.xml
app/src/main/res/values/strings.xml
app/src/main/res/values/themes.xml
app/src/main/res/xml/backup_rules.xml
app/src/main/res/xml/data_extraction_rules.xml
app/src/test/java/com/example/CalculationEnginesTest.kt
app/src/test/java/com/example/ExampleRobolectricTest.kt
app/src/test/java/com/example/ExampleUnitTest.kt
app/src/test/java/com/example/GreetingScreenshotTest.kt
build.gradle.kts
gradle.properties
gradle/libs.versions.toml
gradle/wrapper/gradle-wrapper.properties
gradlew
gradlew.bat
metadata.json
package-lock.json
package.json
preview/app.js
preview/index.html
preview/styles.css
scripts/verify_project.py
settings.gradle.kts
```
