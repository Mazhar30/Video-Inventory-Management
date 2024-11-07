import React, { useEffect, useState } from 'react';
import { fetchVideos, fetchUsers, assignVideoToUsers } from '../../api/api';
import Select from 'react-select';
import ReactPaginate from 'react-paginate';
import '../../static/css/AssignVideos.css';

const AssignVideos = () => {
    const [videos, setVideos] = useState([]);
    const [users, setUsers] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState({});
    const [currentPage, setCurrentPage] = useState(0);
    const videosPerPage = 5; 

    useEffect(() => {
        const loadVideos = async () => {
            try {
                const fetchedVideos = await fetchVideos();
                setVideos(fetchedVideos);
            } catch (error) {
                console.error('Error loading videos:', error);
            }
        };

        const loadUsers = async () => {
            try {
                const fetchedUsers = await fetchUsers();
                setUsers(fetchedUsers);
            } catch (error) {
                console.error('Error loading users:', error);
            }
        };

        loadVideos();
        loadUsers();
    }, []);

    const handleAssign = async (videoId) => {
        const usersToAssign = selectedUsers[videoId];
        if (!usersToAssign || usersToAssign.length === 0) {
            alert("Please select users to assign the video to.");
            return;
        }

        try {
            await assignVideoToUsers(videoId, usersToAssign);
            alert('Video assigned successfully!');

            setVideos(prevVideos =>
                prevVideos.map(video =>
                    video.id === videoId
                        ? { ...video, assignedUsers: [...video.assignedUsers, ...usersToAssign] }
                        : video
                )
            );

            setSelectedUsers(prev => ({ ...prev, [videoId]: [] }));
        } catch (error) {
            console.error('Error assigning video:', error);
            alert('Failed to assign the video.');
        }
    };

    const handleUserSelect = (videoId, selectedOptions) => {
        const selectedIds = selectedOptions.map(option => option.value);
        setSelectedUsers(prev => ({ ...prev, [videoId]: selectedIds }));
    };

    const handlePageChange = ({ selected }) => {
        setCurrentPage(selected);
    };

    const currentVideos = videos.slice(currentPage * videosPerPage, (currentPage + 1) * videosPerPage);

    return (
        <div className="assign-videos-container">
            <h3 className="assign-videos-heading">Assign Videos to Users</h3>

            {videos.length === 0 ? (
                <p>No videos available</p>
            ) : users.length === 0 ? (
                <p>No users available</p>
            ) : (
                <>
                    <table className="videos-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Assigned Users</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentVideos.map(video => {
                                const unassignedUsers = users.filter(user => !video.assignedUsers.includes(user.id));
                                const userOptions = unassignedUsers.map(user => ({ value: user.id, label: user.username }));

                                return (
                                    <tr key={video.id}>
                                        <td>{video.title}</td>
                                        <td>
                                            <Select
                                                options={userOptions}
                                                isMulti
                                                onChange={(selectedOptions) => handleUserSelect(video.id, selectedOptions)}
                                                value={(selectedUsers[video.id] || []).map(userId => ({
                                                    value: userId,
                                                    label: users.find(user => user.id === userId)?.username || ''
                                                }))}
                                                placeholder="Select users..."
                                                className="user-select"
                                            />
                                        </td>
                                        <td className="actions-cell">
                                            <button className="assign-button" onClick={() => handleAssign(video.id)}>
                                                Assign
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>

                    <ReactPaginate
                        previousLabel={'Previous'}
                        nextLabel={'Next'}
                        pageCount={Math.ceil(videos.length / videosPerPage)}
                        onPageChange={handlePageChange}
                        containerClassName={'pagination'}
                        activeClassName={'active'}
                        pageClassName={'page-item'}
                        previousClassName={'page-item'}
                        nextClassName={'page-item'}
                        breakClassName={'page-item'}
                        disabledClassName={'disabled'}
                    />
                </>
            )}
        </div>
    );
};

export default AssignVideos;