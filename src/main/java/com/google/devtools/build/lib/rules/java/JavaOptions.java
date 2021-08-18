// Copyright 2014 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.rules.java;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.EmptyToNullLabelConverter;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.LabelConverter;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.LabelListConverter;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.LabelMapConverter;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.StrictDepsConverter;
import com.google.devtools.build.lib.analysis.config.CoreOptionConverters.StrictDepsMode;
import com.google.devtools.build.lib.analysis.config.FragmentOptions;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.rules.java.JavaConfiguration.ImportDepsCheckingLevel;
import com.google.devtools.build.lib.rules.java.JavaConfiguration.JavaClasspathMode;
import com.google.devtools.build.lib.rules.java.JavaConfiguration.OneVersionEnforcementLevel;
import com.google.devtools.common.options.EnumConverter;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionMetadataTag;
import java.util.List;
import java.util.Map;

/** Command-line options for building Java targets */
public class JavaOptions extends FragmentOptions {
  /** Converter for the --java_classpath option. */
  public static class JavaClasspathModeConverter extends EnumConverter<JavaClasspathMode> {
    public JavaClasspathModeConverter() {
      super(JavaClasspathMode.class, "Java classpath reduction strategy");
    }
  }

  /** Converter for the --experimental_one_version_enforcement option */
  public static class OneVersionEnforcementLevelConverter
      extends EnumConverter<OneVersionEnforcementLevel> {
    public OneVersionEnforcementLevelConverter() {
      super(OneVersionEnforcementLevel.class, "Enforcement level for Java One Version violations");
    }
  }

  /** Converter for the --experimental_import_deps_checking option */
  public static class ImportDepsCheckingLevelConverter
      extends EnumConverter<ImportDepsCheckingLevel> {
    public ImportDepsCheckingLevelConverter() {
      super(
          ImportDepsCheckingLevel.class,
          "Enforcement level for the dependency checking for import targets.");
    }
  }

  @Option(
      name = "experimental_disallow_legacy_java_toolchain_flags",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "If enabled, disallow legacy Java toolchain flags (--javabase, --host_javabase,"
              + " --java_toolchain, --host_java_toolchain) and require the use of --platforms"
              + " instead; see #7849")
  public boolean disallowLegacyJavaToolchainFlags;

  @Deprecated
  @Option(
      name = "javabase",
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "No-op. Kept here for backwards compatibility.")
  public Label javaBase;

  @Deprecated
  @Option(
      name = "java_toolchain",
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "No-op. Kept here for backwards compatibility.")
  public Label javaToolchain;

  @Deprecated
  @Option(
      name = "host_java_toolchain",
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "No-op. Kept here for backwards compatibility.")
  public Label hostJavaToolchain;

  @Deprecated
  @Option(
      name = "host_javabase",
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "No-op.  Kept here for backwards compatibility.")
  public Label hostJavaBase;

  @Option(
      name = "javacopt",
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Additional options to pass to javac.")
  public List<String> javacOpts;

  @Option(
      name = "host_javacopt",
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Additional options to pass to javac when building tools that are executed during a"
              + " build.")
  public List<String> hostJavacOpts;

  @Option(
      name = "jvmopt",
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Additional options to pass to the Java VM. These options will get added to the "
              + "VM startup options of each java_binary target.")
  public List<String> jvmOpts;

  @Option(
      name = "host_jvmopt",
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Additional options to pass to the Java VM when building tools that are executed during "
              + " the build. These options will get added to the VM startup options of each "
              + " java_binary target.")
  public List<String> hostJvmOpts;

  @Option(
      name = "use_ijars",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "If enabled, this option causes Java compilation to use interface jars. "
              + "This will result in faster incremental compilation, "
              + "but error messages can be different.")
  public boolean useIjars;

  @Option(
      name = "java_header_compilation",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Compile ijars directly from source.",
      oldName = "experimental_java_header_compilation")
  public boolean headerCompilation;

  @Option(
      name = "java_deps",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Generate dependency information (for now, compile-time classpath) per Java target.")
  public boolean javaDeps;

  @Option(
      name = "experimental_java_classpath",
      allowMultiple = false,
      defaultValue = "javabuilder",
      converter = JavaClasspathModeConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Enables reduced classpaths for Java compilations.",
      oldName = "java_classpath")
  public JavaClasspathMode javaClasspath;

  @Option(
      name = "experimental_inmemory_jdeps_files",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.BUILD_TIME_OPTIMIZATION,
      effectTags = {
        OptionEffectTag.LOADING_AND_ANALYSIS,
        OptionEffectTag.EXECUTION,
        OptionEffectTag.AFFECTS_OUTPUTS
      },
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "If enabled, the dependency (.jdeps) files generated from Java compilations will be "
              + "passed through in memory directly from the remote build nodes instead of being "
              + "written to disk.")
  public boolean inmemoryJdepsFiles;

  @Option(
      name = "java_debug",
      defaultValue = "null",
      expansion = {
        "--test_arg=--wrapper_script_flag=--debug",
        "--test_output=streamed",
        "--test_strategy=exclusive",
        "--test_timeout=9999",
        "--nocache_test_results"
      },
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Causes the Java virtual machine of a java test to wait for a connection from a "
              + "JDWP-compliant debugger (such as jdb) before starting the test. Implies "
              + "-test_output=streamed.")
  public Void javaTestDebug;

  @Option(
      name = "experimental_strict_java_deps",
      allowMultiple = false,
      defaultValue = "default",
      converter = StrictDepsConverter.class,
      documentationCategory = OptionDocumentationCategory.INPUT_STRICTNESS,
      effectTags = {OptionEffectTag.BUILD_FILE_SEMANTICS, OptionEffectTag.EAGERNESS_TO_EXIT},
      help =
          "If true, checks that a Java target explicitly declares all directly used "
              + "targets as dependencies.",
      oldName = "strict_java_deps")
  public StrictDepsMode strictJavaDeps;

  @Option(
      name = "experimental_fix_deps_tool",
      defaultValue = "add_dep",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.BUILD_FILE_SEMANTICS},
      help = "Specifies which tool should be used to resolve missing dependencies.")
  public String fixDepsTool;

  // TODO(bazel-team): This flag should ideally default to true (and eventually removed). We have
  // been accidentally supplying JUnit and Hamcrest deps to java_test targets indirectly via the
  // BazelTestRunner, and setting this flag to true fixes that behaviour.
  @Option(
      name = "explicit_java_test_deps",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Explicitly specify a dependency to JUnit or Hamcrest in a java_test instead of "
              + " accidentally obtaining from the TestRunner's deps. Only works for bazel right "
              + "now.")
  public boolean explicitJavaTestDeps;

  @Option(
      name = "host_java_launcher",
      defaultValue = "null",
      converter = EmptyToNullLabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "The Java launcher used by tools that are executed during a build.")
  public Label hostJavaLauncher;

  @Option(
      name = "java_launcher",
      defaultValue = "null",
      converter = EmptyToNullLabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "The Java launcher to use when building Java binaries. "
              + " If this flag is set to the empty string, the JDK launcher is used. "
              + "The \"launcher\" attribute overrides this flag. ")
  public Label javaLauncher;

  @Option(
      name = "proguard_top",
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Specifies which version of ProGuard to use for code removal when building a Java "
              + "binary.")
  public Label proguard;

  @Option(
      name = "extra_proguard_specs",
      allowMultiple = true,
      defaultValue = "null",
      converter = LabelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Additional Proguard specs that will be used for all Proguard invocations.  Note that "
              + "using this option only has an effect when Proguard is used anyway.")
  public List<Label> extraProguardSpecs;

  /**
   * Comma-separated list of Mnemonic=label pairs of optimizers to run in the given order, treating
   * {@code Proguard} specially by substituting in the relevant Proguard binary automatically. All
   * optimizers must understand the same flags as Proguard.
   */
  @Option(
      name = "experimental_bytecode_optimizers",
      defaultValue = "Proguard",
      converter = LabelMapConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Do not use.")
  public Map<String, Label> bytecodeOptimizers;

  /**
   * If true, the OPTIMIZATION stage of the bytecode optimizer will be split across multiple
   * actions.
   */
  @Option(
      name = "split_bytecode_optimization_pass",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Do not use.")
  public boolean splitBytecodeOptimizationPass;

  @Option(
      name = "enforce_proguard_file_extension",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.EAGERNESS_TO_EXIT},
      help =
          "If enabled, requires that ProGuard configuration files outside of third_party/ use the"
              + " *.pgcfg file extension.")
  public boolean enforceProguardFileExtension;

  @Option(
      name = "java_optimization_mode",
      defaultValue = "",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Do not use.")
  public String javaOptimizationMode;

  @Option(
      name = "legacy_bazel_java_test",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Use the legacy mode of Bazel for java_test.")
  public boolean legacyBazelJavaTest;

  @Option(
      name = "strict_deps_java_protos",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.BUILD_FILE_SEMANTICS, OptionEffectTag.EAGERNESS_TO_EXIT},
      help =
          "When 'strict-deps' is on, .java files that depend on classes not declared in their "
              + "rule's 'deps' fail to build. In other words, it's forbidden to depend on classes "
              + "obtained transitively. When true, Java protos are strict regardless of their "
              + "'strict_deps' attribute.")
  public boolean strictDepsJavaProtos;

  @Option(
      name = "disallow_strict_deps_for_jpl",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.BUILD_FILE_SEMANTICS, OptionEffectTag.EAGERNESS_TO_EXIT},
      help =
          "If set, any java_proto_library or java_mutable_proto_library which sets the "
              + "strict_deps attribute explicitly will fail to build.")
  public boolean isDisallowStrictDepsForJpl;

  @Option(
      name = "experimental_one_version_enforcement",
      defaultValue = "OFF",
      converter = OneVersionEnforcementLevelConverter.class,
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "When enabled, enforce that a java_binary rule can't contain more than one version "
              + "of the same class file on the classpath. This enforcement can break the build, or "
              + "can just result in warnings.")
  public OneVersionEnforcementLevel enforceOneVersion;

  @Option(
      name = "experimental_import_deps_checking",
      defaultValue = "OFF",
      converter = ImportDepsCheckingLevelConverter.class,
      documentationCategory = OptionDocumentationCategory.INPUT_STRICTNESS,
      effectTags = {OptionEffectTag.LOADING_AND_ANALYSIS},
      help =
          "When enabled, check whether the dependencies of an aar_import are complete. "
              + "This enforcement can break the build, or can just result in warnings.")
  public ImportDepsCheckingLevel importDepsCheckingLevel;

  @Option(
      name = "one_version_enforcement_on_java_tests",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "When enabled, and with experimental_one_version_enforcement set to a non-NONE value,"
              + " enforce one version on java_test targets. This flag can be disabled to improve"
              + " incremental test performance at the expense of missing potential one version"
              + " violations.")
  public boolean enforceOneVersionOnJavaTests;

  @Option(
      name = "experimental_allow_runtime_deps_on_neverlink",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.BUILD_FILE_SEMANTICS},
      metadataTags = {OptionMetadataTag.EXPERIMENTAL},
      help =
          "Flag to help transition from allowing to disallowing runtime_deps on neverlink"
              + " Java archives. The depot needs to be cleaned up to roll this out by default.")
  public boolean allowRuntimeDepsOnNeverLink;

  @Option(
      name = "experimental_add_test_support_to_compile_time_deps",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help =
          "Flag to help transition away from adding test support libraries to the compile-time"
              + " deps of Java test rules.")
  public boolean addTestSupportToCompileTimeDeps;

  @Option(
      name = "experimental_run_android_lint_on_java_rules",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.AFFECTS_OUTPUTS},
      help = "Whether to validate java_* sources.")
  public boolean runAndroidLint;

  @Option(
      name = "experimental_limit_android_lint_to_android_constrained_java",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.AFFECTS_OUTPUTS},
      help = "Limit --experimental_run_android_lint_on_java_rules to Android-compatible libraries.")
  public boolean limitAndroidLintToAndroidCompatible;

  @Option(
      name = "jplPropagateCcLinkParamsStore",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.AFFECTS_OUTPUTS, OptionEffectTag.LOADING_AND_ANALYSIS},
      metadataTags = {OptionMetadataTag.INCOMPATIBLE_CHANGE},
      help = "Roll-out flag for making java_proto_library propagate CcLinkParamsStore. DO NOT USE.")
  public boolean jplPropagateCcLinkParamsStore;

  // Plugins are built using the host config. To avoid cycles we just don't propagate
  // this option to the host config. If one day we decide to use plugins when building
  // host tools, we can improve this by (for example) creating a compiler configuration that is
  // used only for building plugins.
  @Option(
      name = "plugin",
      converter = LabelListConverter.class,
      allowMultiple = true,
      defaultValue = "null",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Plugins to use in the build. Currently works with java_plugin.")
  public List<Label> pluginList;

  @Option(
      name = "incompatible_disallow_resource_jars",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      metadataTags = {
        OptionMetadataTag.INCOMPATIBLE_CHANGE,
        OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES
      },
      help =
          "Disables the resource_jars attribute; use java_import and deps or runtime_deps instead.")
  public boolean disallowResourceJars;

  @Option(
      name = "experimental_java_header_input_pruning",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "If enabled, header compilation actions support --java_classpath=bazel")
  public boolean experimentalJavaHeaderInputPruning;

  @Option(
      name = "experimental_turbine_annotation_processing",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "If enabled, turbine is used for all annotation processing")
  public boolean experimentalTurbineAnnotationProcessing;

  @Option(
      name = "java_runtime_version",
      defaultValue = "local_jdk",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "The Java runtime version")
  public String javaRuntimeVersion;

  @Option(
      name = "tool_java_runtime_version",
      defaultValue = "remotejdk_11",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "The Java runtime version used to execute tools during the build")
  public String hostJavaRuntimeVersion;

  @Option(
      name = "java_language_version",
      defaultValue = "8",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "The Java language version")
  public String javaLanguageVersion;

  @Option(
      name = "tool_java_language_version",
      defaultValue = "8",
      documentationCategory = OptionDocumentationCategory.UNCATEGORIZED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "The Java language version used to execute the tools that are needed during a build")
  public String hostJavaLanguageVersion;

  @Option(
      name = "incompatible_dont_collect_native_libraries_in_data",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      metadataTags = {
        OptionMetadataTag.INCOMPATIBLE_CHANGE,
        OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES
      },
      help = "This flag is a noop and scheduled for removal.")
  public boolean dontCollectDataLibraries;

  @Option(
      name = "incompatible_require_javaplugininfo_in_javacommon",
      defaultValue = "false",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      metadataTags = {
        OptionMetadataTag.INCOMPATIBLE_CHANGE,
        OptionMetadataTag.TRIGGERED_BY_ALL_INCOMPATIBLE_CHANGES
      },
      help = "When enabled java_common.compile only accepts JavaPluginInfo for plugins.")
  public boolean requireJavaPluginInfo;

  @Option(
      name = "experimental_enable_jspecify",
      defaultValue = "true",
      documentationCategory = OptionDocumentationCategory.UNDOCUMENTED,
      effectTags = {OptionEffectTag.UNKNOWN},
      help = "Enable experimental jspecify integration.")
  public boolean experimentalEnableJspecify;
  @Override
  public FragmentOptions getHost() {
    // Note validation actions don't run in host config, so no need copying flags related to that.
    // TODO(b/171078539): revisit if relevant validations are run in host config
    JavaOptions host = (JavaOptions) getDefault();

    if (hostJvmOpts == null || hostJvmOpts.isEmpty()) {
      host.jvmOpts = ImmutableList.of("-XX:ErrorFile=/dev/stderr");
    } else {
      host.jvmOpts = hostJvmOpts;
    }


    host.javacOpts = hostJavacOpts;

    host.javaLauncher = hostJavaLauncher;

    // Java builds often contain complicated code generators for which
    // incremental build performance is important.
    host.useIjars = useIjars;
    host.headerCompilation = headerCompilation;

    host.javaDeps = javaDeps;
    host.javaClasspath = javaClasspath;
    host.inmemoryJdepsFiles = inmemoryJdepsFiles;

    host.strictJavaDeps = strictJavaDeps;
    host.fixDepsTool = fixDepsTool;

    host.enforceOneVersion = enforceOneVersion;
    host.importDepsCheckingLevel = importDepsCheckingLevel;
    // java_test targets can be used as a host tool, Ex: as a validating tool on a genrule.
    host.enforceOneVersionOnJavaTests = enforceOneVersionOnJavaTests;
    host.allowRuntimeDepsOnNeverLink = allowRuntimeDepsOnNeverLink;
    host.addTestSupportToCompileTimeDeps = addTestSupportToCompileTimeDeps;

    host.jplPropagateCcLinkParamsStore = jplPropagateCcLinkParamsStore;

    host.disallowResourceJars = disallowResourceJars;

    host.javaRuntimeVersion = hostJavaRuntimeVersion;
    host.javaLanguageVersion = hostJavaLanguageVersion;

    host.bytecodeOptimizers = bytecodeOptimizers;
    host.splitBytecodeOptimizationPass = splitBytecodeOptimizationPass;

    host.enforceProguardFileExtension = enforceProguardFileExtension;
    host.extraProguardSpecs = extraProguardSpecs;
    host.proguard = proguard;

    // Save host options for further use.
    host.hostJavacOpts = hostJavacOpts;
    host.hostJavaLauncher = hostJavaLauncher;
    host.hostJavaRuntimeVersion = hostJavaRuntimeVersion;
    host.hostJavaLanguageVersion = hostJavaLanguageVersion;

    host.experimentalTurbineAnnotationProcessing = experimentalTurbineAnnotationProcessing;

    host.requireJavaPluginInfo = requireJavaPluginInfo;

    return host;
  }
}
