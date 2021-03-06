(ns cwiki-test.font-detection-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cwiki-mde.font-detection :as ft]))

;;; These tests are very system dependent. They may change depending on the
;;; operating system being used and the fonts actually installed on your
;;; system. They are also dependent on the test host page (test.html)
;;; loading the project CSS. These happen to work for me.

(deftest font-available?-test
  (testing "Correct detection of installed fonts."
    (is (false? (ft/font-available? "Calibri")))
    (is (false? (ft/font-available? "Calibri Regular")))
    (is (true? (ft/font-available? "Arial")))
    (is (false? (ft/font-available? "Boojum")))
    (is (true? (ft/font-available? "Palatino")))
    (is (true? (ft/font-available? "Helvetica")))
    (is (true? (ft/font-available? "Helvetica Neue")))
    (is (false? (ft/font-available? "Helvetica Newish")))))

(deftest font-family->font-used-test
  (testing "Correct selection of an installed font from a font family.")
  (is (= "Muli" (ft/font-family->font-used "Century Gothic, Muli, Segoe UI, Arial, sans-serif")))
  (is (= "Palatino" (ft/font-family->font-used "Palatino, Palatino Linotype, Palatino LT STD, Book Antiqua, Georgia, serif")))
  (is (= "Ubuntu Mono" (ft/font-family->font-used "Consolas, Ubuntu Mono, Menlo, Monaco, Lucida Console,\n    Liberation Mono, DejaVu Sans Mono, Bitstream Vera Sans Mono,\n    Courier New, monospace, serif")))
  (is (= "Helvetica Neue" (ft/font-family->font-used "Calibri, Segoe UI, Candara, Helvetica Neue,\n    Lucida Grande, Tahoma, Verdana, Helvetica, Arial, sans-serif"))))