import sbtcrossproject.crossProject

crossScalaVersions in ThisBuild := Seq("2.12.19", "2.13.14")
scalaVersion in ThisBuild := (crossScalaVersions in ThisBuild).value.head

val commonSettings: Seq[Setting[_]] = Seq(
  version := "1.0.1-SNAPSHOT",
  organization := "org.scala-native",
  scalacOptions ++= Seq("-deprecation", "-feature"),

  homepage := Some(url("http://scala-native.org/")),
  licenses += ("BSD New",
      url("https://github.com/scala-native/scala-native-java-logging/blob/main/LICENSE")),
  scmInfo := Some(ScmInfo(
      url("https://github.com/scala-native/scala-native-java-logging"),
      "scm:git:git@github.com:scala-native/scala-native-java-logging.git",
      Some("scm:git:git@github.com:scala-native/scala-native-java-logging.git")))
)

lazy val root: Project = project.in(file(".")).
  enablePlugins(ScalaNativePlugin).
  settings(commonSettings).
  settings(
    name := "scala-native-java-logging",

    mappings in (Compile, packageBin) ~= {
      _.filter(!_._2.endsWith(".class"))
    },
    exportJars := true,

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
        <developers>
          <developer>
            <id>sjrd</id>
            <name>Sébastien Doeraene</name>
            <url>https://github.com/sjrd/</url>
          </developer>
          <developer>
            <id>gzm0</id>
            <name>Tobias Schlatter</name>
            <url>https://github.com/gzm0/</url>
          </developer>
          <developer>
            <id>nicolasstucki</id>
            <name>Nicolas Stucki</name>
            <url>https://github.com/nicolasstucki/</url>
          </developer>
        </developers>
    ),
    pomIncludeRepository := { _ => false }
  )

lazy val testSuite = crossProject(NativePlatform, JVMPlatform).
  nativeConfigure(_.enablePlugins(ScalaNativeJUnitPlugin)).
  settings(commonSettings: _*).
  settings(
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a")
  ).
  nativeSettings(
    name := "java.logging testSuite on Native"
  ).
  nativeConfigure(_.dependsOn(root)).
  jvmSettings(
    name := "java.logging testSuite on JVM",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test"
  )
