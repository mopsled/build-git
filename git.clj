(ns playground.git)

(defn git [name]
  (let [master-branch {:name "master"
                       :commit nil}]
    {:name name
     :head master-branch
     :last-commit-id -1}))

(defn commit [repo message]
  (let
    [commit-id (inc (:last-commit-id repo))
     this-commit {:id commit-id
                  :message message
                  :parent (:commit (:head repo))}]
    {:name (:name repo)
     :last-commit-id commit-id
     :head {:name (:name (:head repo))
            :commit this-commit}}))

(defn inner-log [a-commit]
  (let [parent (:parent a-commit)]
    (if parent
      (conj (inner-log parent) a-commit)
      (list a-commit))))

(defn log [repo] (inner-log (:commit (:head repo))))

;; Poor-man's testing

(def repo (git "test"))
(def repo (commit repo "Inital commit"))
(def repo (commit repo "Change 1"))

(= (count (log repo)) 2) ;; Should have two commits
(= (:id (first (log repo))) 1) ;; Commit 1 should be first
(= (:id (second (log repo))) 0) ;; Commit 0 should be second