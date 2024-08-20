rootProject.name = "sample-app"

includeBuild("..")
//includeBuild("../randomizer-lib")
includeBuild(".")
include("src:test:ab.cd")
findProject(":src:test:ab.cd")?.name = "ab.cd"
