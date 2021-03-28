require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-push-notifier"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/jafar-jabr/react-native-push-notifier.git", :tag => "#{s.version}" }

   s.preserve_paths = 'LICENSE', 'README.md', 'package.json', 'index.js'
   s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.dependency "React-Core"
end
